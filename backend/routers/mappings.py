"""API mapping endpoints."""

from fastapi import APIRouter, Query
from typing import Optional
from ..database import get_db, rows_to_dicts, row_to_dict

router = APIRouter(prefix="/api/mappings", tags=["API Mappings"])


@router.get("")
def list_mappings(
    subsystem: Optional[str] = None,
    score_min: Optional[float] = None,
    score_max: Optional[float] = None,
    effort: Optional[str] = None,
    mapping_type: Optional[str] = None,
    paradigm_shift: Optional[bool] = None,
    needs_ui_rewrite: Optional[bool] = None,
    search: Optional[str] = None,
    page: int = Query(1, ge=1),
    page_size: int = Query(50, ge=1, le=200),
):
    with get_db() as db:
        where = []
        params = []

        if subsystem:
            where.append("aa.subsystem = ?")
            params.append(subsystem)
        if score_min is not None:
            where.append("m.score >= ?")
            params.append(score_min)
        if score_max is not None:
            where.append("m.score <= ?")
            params.append(score_max)
        if effort:
            where.append("m.effort_level = ?")
            params.append(effort)
        if mapping_type:
            where.append("m.mapping_type = ?")
            params.append(mapping_type)
        if paradigm_shift is not None:
            where.append("m.paradigm_shift = ?")
            params.append(paradigm_shift)
        if needs_ui_rewrite is not None:
            where.append("m.needs_ui_rewrite = ?")
            params.append(needs_ui_rewrite)
        if search:
            where.append("(aa.name LIKE ? OR aa.signature LIKE ? OR oa.name LIKE ?)")
            params.extend([f"%{search}%", f"%{search}%", f"%{search}%"])

        where_clause = " AND ".join(where) if where else "1=1"

        count = db.execute(f"""
            SELECT COUNT(*) FROM api_mappings m
            JOIN android_apis aa ON m.android_api_id = aa.id
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            WHERE {where_clause}
        """, params).fetchone()[0]

        offset = (page - 1) * page_size
        rows = db.execute(f"""
            SELECT m.id, m.score, m.confidence, m.mapping_type, m.effort_level,
                   m.paradigm_shift, m.needs_ui_rewrite, m.gap_description,
                   aa.id as android_api_id, aa.name as android_api_name,
                   aa.signature as android_signature, aa.kind as android_kind,
                   aa.subsystem,
                   at2.name as android_type_name, ap.name as android_package,
                   oa.id as oh_api_id, oa.name as oh_api_name,
                   oa.signature as oh_signature, oa.kind as oh_kind,
                   ot.name as oh_type_name, om.name as oh_module
            FROM api_mappings m
            JOIN android_apis aa ON m.android_api_id = aa.id
            JOIN android_types at2 ON aa.type_id = at2.id
            JOIN android_packages ap ON at2.package_id = ap.id
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_types ot ON oa.type_id = ot.id
            LEFT JOIN oh_modules om ON oa.module_id = om.id
            WHERE {where_clause}
            ORDER BY m.score ASC, aa.name
            LIMIT ? OFFSET ?
        """, params + [page_size, offset]).fetchall()

        return {
            "items": rows_to_dicts(rows),
            "total": count,
            "page": page,
            "page_size": page_size,
            "total_pages": (count + page_size - 1) // page_size,
        }


@router.get("/{mapping_id}")
def get_mapping(mapping_id: int):
    with get_db() as db:
        row = db.execute("""
            SELECT m.*,
                   aa.name as android_api_name, aa.signature as android_signature,
                   aa.kind as android_kind, aa.return_type as android_return_type,
                   at2.name as android_type_name, at2.kind as android_type_kind,
                   at2.extends_type, at2.implements as type_implements,
                   ap.name as android_package, ap.subsystem as android_subsystem,
                   oa.name as oh_api_name, oa.signature as oh_signature,
                   oa.kind as oh_kind, oa.return_type as oh_return_type,
                   oa.syscap as oh_syscap, oa.since_version as oh_since,
                   ot.name as oh_type_name, ot.kind as oh_type_kind,
                   om.name as oh_module, om.sdk_type as oh_sdk_type, om.kit as oh_kit
            FROM api_mappings m
            JOIN android_apis aa ON m.android_api_id = aa.id
            JOIN android_types at2 ON aa.type_id = at2.id
            JOIN android_packages ap ON at2.package_id = ap.id
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_types ot ON oa.type_id = ot.id
            LEFT JOIN oh_modules om ON oa.module_id = om.id
            WHERE m.id = ?
        """, (mapping_id,)).fetchone()
        return row_to_dict(row)


@router.get("/by-android/{android_api_id}")
def get_mappings_for_android_api(android_api_id: int):
    with get_db() as db:
        rows = db.execute("""
            SELECT m.*, oa.name as oh_api_name, oa.signature as oh_signature,
                   oa.kind as oh_kind, ot.name as oh_type_name, om.name as oh_module
            FROM api_mappings m
            LEFT JOIN oh_apis oa ON m.oh_api_id = oa.id
            LEFT JOIN oh_types ot ON oa.type_id = ot.id
            LEFT JOIN oh_modules om ON oa.module_id = om.id
            WHERE m.android_api_id = ?
            ORDER BY m.score DESC
        """, (android_api_id,)).fetchall()
        return rows_to_dicts(rows)
