"""OpenHarmony API endpoints."""

from fastapi import APIRouter, Query
from typing import Optional
from ..database import get_db, rows_to_dicts, row_to_dict

router = APIRouter(prefix="/api/oh", tags=["OpenHarmony APIs"])


@router.get("/modules")
def list_modules(
    subsystem: Optional[str] = None,
    sdk_type: Optional[str] = None,
):
    with get_db() as db:
        where = []
        params = []
        if subsystem:
            where.append("m.subsystem = ?")
            params.append(subsystem)
        if sdk_type:
            where.append("m.sdk_type = ?")
            params.append(sdk_type)
        where_clause = " AND ".join(where) if where else "1=1"

        rows = db.execute(f"""
            SELECT m.*,
                   COUNT(DISTINCT t.id) as type_count,
                   (SELECT COUNT(*) FROM oh_apis a WHERE a.module_id = m.id) as api_count
            FROM oh_modules m
            LEFT JOIN oh_types t ON t.module_id = m.id
            WHERE {where_clause}
            GROUP BY m.id ORDER BY m.name
        """, params).fetchall()
        return rows_to_dicts(rows)


@router.get("/modules/{module_id}/types")
def list_module_types(module_id: int):
    with get_db() as db:
        rows = db.execute("""
            SELECT t.*, m.name as module_name,
                   (SELECT COUNT(*) FROM oh_apis a WHERE a.type_id = t.id) as api_count
            FROM oh_types t
            JOIN oh_modules m ON t.module_id = m.id
            WHERE t.module_id = ?
            ORDER BY t.name
        """, (module_id,)).fetchall()
        return rows_to_dicts(rows)


@router.get("/apis/{api_id}")
def get_api(api_id: int):
    with get_db() as db:
        row = db.execute("""
            SELECT a.*, t.name as type_name, t.kind as type_kind,
                   m.name as module_name, m.sdk_type, m.kit
            FROM oh_apis a
            LEFT JOIN oh_types t ON a.type_id = t.id
            JOIN oh_modules m ON a.module_id = m.id
            WHERE a.id = ?
        """, (api_id,)).fetchone()
        return row_to_dict(row)


@router.get("/apis")
def list_apis(
    module: Optional[str] = None,
    subsystem: Optional[str] = None,
    sdk_type: Optional[str] = None,
    kind: Optional[str] = None,
    search: Optional[str] = None,
    page: int = Query(1, ge=1),
    page_size: int = Query(50, ge=1, le=200),
):
    with get_db() as db:
        where = []
        params = []

        if module:
            where.append("m.name = ?")
            params.append(module)
        if subsystem:
            where.append("a.subsystem = ?")
            params.append(subsystem)
        if sdk_type:
            where.append("m.sdk_type = ?")
            params.append(sdk_type)
        if kind:
            where.append("a.kind = ?")
            params.append(kind)
        if search:
            where.append("(a.name LIKE ? OR a.signature LIKE ?)")
            params.extend([f"%{search}%", f"%{search}%"])

        where_clause = " AND ".join(where) if where else "1=1"

        count = db.execute(f"""
            SELECT COUNT(*) FROM oh_apis a
            JOIN oh_modules m ON a.module_id = m.id
            WHERE {where_clause}
        """, params).fetchone()[0]

        offset = (page - 1) * page_size
        rows = db.execute(f"""
            SELECT a.*, COALESCE(t.name, '') as type_name, m.name as module_name, m.sdk_type
            FROM oh_apis a
            LEFT JOIN oh_types t ON a.type_id = t.id
            JOIN oh_modules m ON a.module_id = m.id
            WHERE {where_clause}
            ORDER BY a.name
            LIMIT ? OFFSET ?
        """, params + [page_size, offset]).fetchall()

        return {
            "items": rows_to_dicts(rows),
            "total": count,
            "page": page,
            "page_size": page_size,
            "total_pages": (count + page_size - 1) // page_size,
        }
