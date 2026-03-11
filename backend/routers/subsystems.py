"""Subsystem detail endpoints."""

from fastapi import APIRouter
from ..database import get_db, rows_to_dicts, row_to_dict

router = APIRouter(prefix="/api/subsystems", tags=["Subsystems"])


@router.get("")
def list_subsystems():
    with get_db() as db:
        rows = db.execute("""
            SELECT * FROM subsystems ORDER BY api_count_android DESC
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/{name}")
def get_subsystem(name: str):
    with get_db() as db:
        sub = db.execute("SELECT * FROM subsystems WHERE name = ?", (name,)).fetchone()
        if not sub:
            return None
        result = dict(sub)

        # Score distribution within this subsystem
        result["score_distribution"] = rows_to_dicts(db.execute("""
            SELECT
                CASE
                    WHEN compat_score >= 9 THEN '9-10'
                    WHEN compat_score >= 7 THEN '7-8'
                    WHEN compat_score >= 5 THEN '5-6'
                    WHEN compat_score >= 3 THEN '3-4'
                    ELSE '1-2'
                END as bucket,
                COUNT(*) as count
            FROM android_apis
            WHERE subsystem = ?
            GROUP BY bucket ORDER BY MIN(compat_score)
        """, (name,)).fetchall())

        # Effort distribution
        result["effort_distribution"] = rows_to_dicts(db.execute("""
            SELECT m.effort_level, COUNT(*) as count
            FROM api_mappings m
            JOIN android_apis a ON m.android_api_id = a.id
            WHERE a.subsystem = ?
            GROUP BY m.effort_level
        """, (name,)).fetchall())

        # Top types in this subsystem
        result["types"] = rows_to_dicts(db.execute("""
            SELECT t.id, t.name, t.kind, p.name as package_name,
                   COUNT(a.id) as api_count,
                   ROUND(AVG(a.compat_score), 1) as avg_score
            FROM android_types t
            JOIN android_packages p ON t.package_id = p.id
            JOIN android_apis a ON a.type_id = t.id
            WHERE a.subsystem = ?
            GROUP BY t.id
            ORDER BY api_count DESC
            LIMIT 50
        """, (name,)).fetchall())

        # Top gaps (lowest scoring APIs)
        result["top_gaps"] = rows_to_dicts(db.execute("""
            SELECT a.id, a.name, a.signature, a.compat_score,
                   t.name as type_name, p.name as package_name
            FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            JOIN android_packages p ON t.package_id = p.id
            WHERE a.subsystem = ? AND a.compat_score < 3
            ORDER BY a.compat_score ASC
            LIMIT 20
        """, (name,)).fetchall())

        return result
