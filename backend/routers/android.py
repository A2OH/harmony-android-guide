"""Android API endpoints."""

from fastapi import APIRouter, Query
from typing import Optional
from ..database import get_db, rows_to_dicts, row_to_dict

router = APIRouter(prefix="/api/android", tags=["Android APIs"])


@router.get("/packages")
def list_packages(subsystem: Optional[str] = None):
    with get_db() as db:
        if subsystem:
            rows = db.execute("""
                SELECT p.*, COUNT(DISTINCT t.id) as type_count,
                       (SELECT COUNT(*) FROM android_apis a JOIN android_types t2 ON a.type_id=t2.id WHERE t2.package_id=p.id) as api_count
                FROM android_packages p
                LEFT JOIN android_types t ON t.package_id = p.id
                WHERE p.subsystem = ?
                GROUP BY p.id ORDER BY p.name
            """, (subsystem,)).fetchall()
        else:
            rows = db.execute("""
                SELECT p.*, COUNT(DISTINCT t.id) as type_count,
                       (SELECT COUNT(*) FROM android_apis a JOIN android_types t2 ON a.type_id=t2.id WHERE t2.package_id=p.id) as api_count
                FROM android_packages p
                LEFT JOIN android_types t ON t.package_id = p.id
                GROUP BY p.id ORDER BY p.name
            """).fetchall()
        return rows_to_dicts(rows)


@router.get("/packages/{package_name}/types")
def list_types(package_name: str):
    with get_db() as db:
        rows = db.execute("""
            SELECT t.*, p.name as package_name,
                   (SELECT COUNT(*) FROM android_apis a WHERE a.type_id = t.id) as api_count
            FROM android_types t
            JOIN android_packages p ON t.package_id = p.id
            WHERE p.name = ?
            ORDER BY t.name
        """, (package_name,)).fetchall()
        return rows_to_dicts(rows)


@router.get("/types/{type_id}")
def get_type(type_id: int):
    with get_db() as db:
        row = db.execute("""
            SELECT t.*, p.name as package_name
            FROM android_types t
            JOIN android_packages p ON t.package_id = p.id
            WHERE t.id = ?
        """, (type_id,)).fetchone()
        return row_to_dict(row)


@router.get("/types/{type_id}/apis")
def list_type_apis(
    type_id: int,
    kind: Optional[str] = None,
    score_min: Optional[float] = None,
    score_max: Optional[float] = None,
):
    with get_db() as db:
        query = "SELECT * FROM android_apis WHERE type_id = ?"
        params = [type_id]
        if kind:
            query += " AND kind = ?"
            params.append(kind)
        if score_min is not None:
            query += " AND compat_score >= ?"
            params.append(score_min)
        if score_max is not None:
            query += " AND compat_score <= ?"
            params.append(score_max)
        query += " ORDER BY kind, name"
        rows = db.execute(query, params).fetchall()
        return rows_to_dicts(rows)


@router.get("/apis/{api_id}")
def get_api(api_id: int):
    with get_db() as db:
        row = db.execute("""
            SELECT a.*, t.name as type_name, t.kind as type_kind,
                   p.name as package_name, p.subsystem as package_subsystem
            FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            WHERE a.id = ?
        """, (api_id,)).fetchone()
        if not row:
            return None
        result = dict(row)

        # Get mapping
        mapping = db.execute("""
            SELECT m.*, oa.name as oh_api_name, oa.signature as oh_signature,
                   oa.kind as oh_kind, ot.name as oh_type_name, om.name as oh_module
            FROM api_mappings m
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_types ot ON oa.type_id = ot.id
            LEFT JOIN oh_modules om ON oa.module_id = om.id
            WHERE m.android_api_id = ?
            ORDER BY m.score DESC LIMIT 5
        """, (api_id,)).fetchall()
        result['mappings'] = rows_to_dicts(mapping)
        return result


@router.get("/apis")
def list_apis(
    package: Optional[str] = None,
    subsystem: Optional[str] = None,
    kind: Optional[str] = None,
    score_min: Optional[float] = None,
    score_max: Optional[float] = None,
    effort: Optional[str] = None,
    deprecated: Optional[bool] = None,
    search: Optional[str] = None,
    page: int = Query(1, ge=1),
    page_size: int = Query(50, ge=1, le=200),
):
    with get_db() as db:
        where = []
        params = []

        if package:
            where.append("p.name = ?")
            params.append(package)
        if subsystem:
            where.append("a.subsystem = ?")
            params.append(subsystem)
        if kind:
            where.append("a.kind = ?")
            params.append(kind)
        if score_min is not None:
            where.append("a.compat_score >= ?")
            params.append(score_min)
        if score_max is not None:
            where.append("a.compat_score <= ?")
            params.append(score_max)
        if effort:
            where.append("a.effort_level = ?")
            params.append(effort)
        if deprecated is not None:
            where.append("a.is_deprecated = ?")
            params.append(deprecated)
        if search:
            where.append("(a.name LIKE ? OR a.signature LIKE ?)")
            params.extend([f"%{search}%", f"%{search}%"])

        where_clause = " AND ".join(where) if where else "1=1"

        # Count
        count = db.execute(f"""
            SELECT COUNT(*) FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            WHERE {where_clause}
        """, params).fetchone()[0]

        # Fetch page
        offset = (page - 1) * page_size
        rows = db.execute(f"""
            SELECT a.*, t.name as type_name, p.name as package_name
            FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            WHERE {where_clause}
            ORDER BY a.compat_score ASC NULLS LAST, a.name
            LIMIT ? OFFSET ?
        """, params + [page_size, offset]).fetchall()

        return {
            "items": rows_to_dicts(rows),
            "total": count,
            "page": page,
            "page_size": page_size,
            "total_pages": (count + page_size - 1) // page_size,
        }
