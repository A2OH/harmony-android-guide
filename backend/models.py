"""Pydantic models for API responses."""

from pydantic import BaseModel
from typing import Optional, List


class AndroidPackage(BaseModel):
    id: int
    name: str
    subsystem: str
    description: Optional[str] = None
    type_count: Optional[int] = None
    api_count: Optional[int] = None


class AndroidType(BaseModel):
    id: int
    package_id: int
    package_name: Optional[str] = None
    name: str
    full_name: str
    kind: str
    is_abstract: bool = False
    is_final: bool = False
    is_static: bool = False
    is_deprecated: bool = False
    extends_type: Optional[str] = None
    implements: Optional[str] = None
    api_count: Optional[int] = None


class AndroidApi(BaseModel):
    id: int
    type_id: int
    type_name: Optional[str] = None
    package_name: Optional[str] = None
    kind: str
    name: str
    signature: str
    return_type: Optional[str] = None
    is_deprecated: bool = False
    is_static: bool = False
    subsystem: Optional[str] = None
    compat_score: Optional[float] = None
    effort_level: Optional[str] = None
    compat_summary: Optional[str] = None
    migration_notes: Optional[str] = None


class OhModule(BaseModel):
    id: int
    name: str
    sdk_type: str
    subsystem: str
    file_path: Optional[str] = None
    kit: Optional[str] = None
    syscap: Optional[str] = None
    since_version: Optional[int] = None
    type_count: Optional[int] = None
    api_count: Optional[int] = None


class OhApi(BaseModel):
    id: int
    type_id: Optional[int] = None
    module_id: int
    module_name: Optional[str] = None
    type_name: Optional[str] = None
    kind: str
    name: str
    signature: str
    return_type: Optional[str] = None
    is_deprecated: bool = False
    subsystem: Optional[str] = None
    syscap: Optional[str] = None
    since_version: Optional[int] = None


class ApiMapping(BaseModel):
    id: int
    android_api_id: int
    oh_api_id: Optional[int] = None
    score: float
    confidence: float = 0.5
    mapping_type: str
    effort_level: str
    android_description: Optional[str] = None
    oh_description: Optional[str] = None
    gap_description: Optional[str] = None
    migration_guide: Optional[str] = None
    code_example_android: Optional[str] = None
    code_example_oh: Optional[str] = None
    paradigm_shift: bool = False
    needs_native: bool = False
    needs_ui_rewrite: bool = False
    # Joined fields
    android_api_name: Optional[str] = None
    android_signature: Optional[str] = None
    android_type_name: Optional[str] = None
    android_package: Optional[str] = None
    oh_api_name: Optional[str] = None
    oh_signature: Optional[str] = None
    oh_type_name: Optional[str] = None
    oh_module: Optional[str] = None


class SubsystemSummary(BaseModel):
    id: int
    name: str
    api_count_android: int = 0
    api_count_oh: int = 0
    overall_score: Optional[float] = None
    coverage_pct: float = 0
    description: Optional[str] = None


class StatsOverview(BaseModel):
    total_android_apis: int
    total_android_types: int
    total_android_packages: int
    total_oh_apis: int
    total_oh_types: int
    total_oh_modules: int
    total_mappings: int
    mapped_count: int
    unmapped_count: int
    avg_score: float
    score_distribution: dict
    effort_distribution: dict


class SearchResult(BaseModel):
    platform: str
    id: int
    name: str
    signature: str
    kind: str
    type_name: Optional[str] = None
    package_or_module: Optional[str] = None
    subsystem: Optional[str] = None
    score: Optional[float] = None


class PaginatedResponse(BaseModel):
    items: list
    total: int
    page: int
    page_size: int
    total_pages: int
