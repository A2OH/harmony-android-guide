"""Statistics and dashboard endpoints."""

from fastapi import APIRouter
from ..database import get_db, rows_to_dicts

router = APIRouter(prefix="/api/stats", tags=["Statistics"])


@router.get("/overview")
def get_overview():
    with get_db() as db:
        stats = {}

        stats["total_android_apis"] = db.execute("SELECT COUNT(*) FROM android_apis").fetchone()[0]
        stats["total_android_types"] = db.execute("SELECT COUNT(*) FROM android_types").fetchone()[0]
        stats["total_android_packages"] = db.execute("SELECT COUNT(*) FROM android_packages").fetchone()[0]
        stats["total_oh_apis"] = db.execute("SELECT COUNT(*) FROM oh_apis").fetchone()[0]
        stats["total_oh_types"] = db.execute("SELECT COUNT(*) FROM oh_types").fetchone()[0]
        stats["total_oh_modules"] = db.execute("SELECT COUNT(*) FROM oh_modules").fetchone()[0]
        stats["total_mappings"] = db.execute("SELECT COUNT(*) FROM api_mappings").fetchone()[0]
        stats["mapped_count"] = db.execute("SELECT COUNT(*) FROM api_mappings WHERE oh_api_id IS NOT NULL").fetchone()[0]
        stats["unmapped_count"] = db.execute("SELECT COUNT(*) FROM api_mappings WHERE oh_api_id IS NULL").fetchone()[0]
        stats["avg_score"] = db.execute("SELECT AVG(score) FROM api_mappings").fetchone()[0] or 0

        # Score distribution
        score_dist = {}
        for row in db.execute("""
            SELECT CAST(score AS INTEGER) as bucket, COUNT(*) as cnt
            FROM api_mappings GROUP BY bucket ORDER BY bucket
        """).fetchall():
            score_dist[str(row["bucket"])] = row["cnt"]
        stats["score_distribution"] = score_dist

        # Effort distribution
        effort_dist = {}
        for row in db.execute("""
            SELECT effort_level, COUNT(*) as cnt
            FROM api_mappings GROUP BY effort_level ORDER BY cnt DESC
        """).fetchall():
            effort_dist[row["effort_level"]] = row["cnt"]
        stats["effort_distribution"] = effort_dist

        return stats


@router.get("/subsystems")
def get_subsystem_stats():
    with get_db() as db:
        rows = db.execute("""
            SELECT * FROM subsystems ORDER BY api_count_android DESC
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/score-distribution")
def get_score_distribution():
    with get_db() as db:
        rows = db.execute("""
            SELECT
                CASE
                    WHEN score >= 9 THEN '9-10 (Direct)'
                    WHEN score >= 7 THEN '7-8 (Near)'
                    WHEN score >= 5 THEN '5-6 (Partial)'
                    WHEN score >= 3 THEN '3-4 (Hard)'
                    ELSE '1-2 (Gap)'
                END as bucket,
                COUNT(*) as count,
                ROUND(100.0 * COUNT(*) / (SELECT COUNT(*) FROM api_mappings), 1) as percentage
            FROM api_mappings
            GROUP BY bucket
            ORDER BY MIN(score)
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/coverage-by-subsystem")
def get_coverage_by_subsystem():
    with get_db() as db:
        rows = db.execute("""
            SELECT
                a.subsystem,
                COUNT(*) as total_apis,
                COUNT(CASE WHEN a.compat_score >= 5 THEN 1 END) as well_mapped,
                COUNT(CASE WHEN a.compat_score >= 3 AND a.compat_score < 5 THEN 1 END) as partially_mapped,
                COUNT(CASE WHEN a.compat_score < 3 OR a.compat_score IS NULL THEN 1 END) as gaps,
                ROUND(AVG(COALESCE(a.compat_score, 1)), 1) as avg_score,
                ROUND(100.0 * COUNT(CASE WHEN a.compat_score >= 5 THEN 1 END) / COUNT(*), 1) as coverage_pct
            FROM android_apis a
            WHERE a.subsystem IS NOT NULL
            GROUP BY a.subsystem
            HAVING COUNT(*) >= 5
            ORDER BY avg_score DESC
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/effort-breakdown")
def get_effort_breakdown():
    with get_db() as db:
        rows = db.execute("""
            SELECT
                m.effort_level,
                COUNT(*) as count,
                ROUND(100.0 * COUNT(*) / (SELECT COUNT(*) FROM api_mappings), 1) as percentage
            FROM api_mappings m
            GROUP BY m.effort_level
            ORDER BY
                CASE m.effort_level
                    WHEN 'trivial' THEN 1
                    WHEN 'easy' THEN 2
                    WHEN 'moderate' THEN 3
                    WHEN 'hard' THEN 4
                    WHEN 'rewrite' THEN 5
                    WHEN 'impossible' THEN 6
                    ELSE 7
                END
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/gaps")
def get_top_gaps():
    """Get APIs with lowest compatibility scores — biggest blockers."""
    with get_db() as db:
        rows = db.execute("""
            SELECT
                a.subsystem,
                COUNT(CASE WHEN a.compat_score < 3 THEN 1 END) as gap_count,
                COUNT(*) as total,
                GROUP_CONCAT(DISTINCT
                    CASE WHEN a.compat_score < 2
                    THEN t.name END
                ) as blocked_types
            FROM android_apis a
            JOIN android_types t ON a.type_id = t.id
            WHERE a.subsystem IS NOT NULL
            GROUP BY a.subsystem
            HAVING gap_count > 0
            ORDER BY gap_count DESC
            LIMIT 20
        """).fetchall()
        return rows_to_dicts(rows)


@router.get("/mapping-types")
def get_mapping_type_distribution():
    with get_db() as db:
        rows = db.execute("""
            SELECT mapping_type, COUNT(*) as count,
                   ROUND(AVG(score), 1) as avg_score
            FROM api_mappings
            GROUP BY mapping_type
            ORDER BY count DESC
        """).fetchall()
        return rows_to_dicts(rows)
