export type Lang = 'en' | 'zh';

export const translations: Record<Lang, Record<string, string>> = {
  en: {
    // Header
    'siteName': 'Android → OH Portal',
    'nav.dashboard': 'Dashboard',
    'nav.browse': 'Browse',
    'nav.status': 'Status',
    'nav.mappings': 'Mappings',
    'nav.gaps': 'Gaps',
    'nav.docs': 'Docs',
    'search.placeholder': 'Search APIs... (e.g. Activity, startAbility, Canvas)',

    // DbLoader
    'status.init': 'Initializing...',
    'status.wasm': 'Loading SQL engine...',
    'status.downloading': 'Downloading database (~10 MB)...',
    'status.error': 'Failed to load database. Please refresh.',
    'loader.title': 'Android → OpenHarmony Migration Portal',
    'loader.connecting': 'Connecting...',
    'loader.retry': 'Retry',

    // Dashboard
    'dashboard.title': 'Android ↔ OpenHarmony API Compatibility',
    'stat.androidApis': 'Android APIs',
    'stat.ohApis': 'OH APIs',
    'stat.mapped': 'Mapped',
    'stat.unmapped': 'Unmapped',
    'stat.avgScore': 'Avg Score',
    'stat.totalMappings': 'Total Mappings',
    'chart.scoreDistribution': 'Score Distribution',
    'chart.migrationEffort': 'Migration Effort',
    'table.coverageBySubsystem': 'Coverage by Subsystem',
    'table.subsystem': 'Subsystem',
    'table.apis': 'APIs',
    'table.wellMapped': 'Well Mapped',
    'table.partial': 'Partial',
    'table.gaps': 'Gaps',
    'table.avgScore': 'Avg Score',
    'table.coverage': 'Coverage',
    'dashboard.compatSummary': 'Compatibility Summary',
    'loading': 'Loading...',

    // Browse
    'browse.androidPackages': 'Android Packages',
    'browse.all': 'All',
    'browse.filterByName': 'Filter by name...',
    'browse.allScores': 'All Scores',
    'browse.allEffort': 'All Effort',
    'browse.allTypes': 'All Types',
    'browse.results': '{count} results',

    // Score filter options
    'score.8-10': '8-10 (Direct)',
    'score.5-7': '5-7 (Partial)',
    'score.3-4': '3-4 (Hard)',
    'score.1-2': '1-2 (Gap)',

    // Effort options
    'effort.trivial': 'Trivial',
    'effort.easy': 'Easy',
    'effort.moderate': 'Moderate',
    'effort.hard': 'Hard',
    'effort.rewrite': 'Rewrite',
    'effort.impossible': 'Impossible',

    // Type options
    'type.methods': 'Methods',
    'type.fields': 'Fields',
    'type.constructors': 'Constructors',
    'type.enumConstants': 'Enum Constants',

    // Pagination
    'pagination.prev': 'Prev',
    'pagination.next': 'Next',
    'pagination.pageOf': 'Page {page} of {total}',

    // ApiDetail
    'apiDetail.class': 'Class',
    'apiDetail.signature': 'Signature',
    'apiDetail.returns': 'Returns',
    'apiDetail.type': 'Type',
    'apiDetail.deprecated': 'Deprecated',
    'apiDetail.mappingDetails': 'Mapping Details',
    'apiDetail.score': 'Score',
    'apiDetail.mappingType': 'Mapping Type',
    'apiDetail.effort': 'Effort',
    'apiDetail.flags': 'Flags',
    'apiDetail.paradigmShift': 'paradigm shift',
    'apiDetail.uiRewrite': 'UI rewrite',
    'apiDetail.gapAnalysis': 'Gap Analysis',
    'apiDetail.migrationGuide': 'Migration Guide',
    'apiDetail.alternativeMappings': 'Alternative Mappings',
    'apiDetail.noEquivalent': 'No OpenHarmony equivalent found',

    // Mappings
    'mappings.title': 'API Mappings',
    'mappings.searchPlaceholder': 'Search mappings...',
    'mappings.androidApi': 'Android API',
    'mappings.ohApi': 'OH API',
    'mappings.type': 'Type',
    'mappings.effort': 'Effort',
    'mappings.flags': 'Flags',
    'mappings.noMapping': 'No mapping',
    'mappings.count': '{count} mappings',
    'mappings.allTypes': 'All Types',
    'mappings.direct': 'Direct',
    'mappings.near': 'Near',
    'mappings.partial': 'Partial',
    'mappings.composite': 'Composite',
    'mappings.none': 'None',

    // Gaps
    'gaps.title': 'API Gaps',
    'gaps.description': 'Android APIs with compatibility score ≤ 3 — these require significant effort or have no OpenHarmony equivalent.',
    'gaps.filterPlaceholder': 'Filter gaps...',
    'gaps.count': '{count} gaps',

    // Search
    'search.title': 'Search Results',
    'search.emptyPrompt': 'Enter a search query in the header to find APIs.',
    'search.searching': 'Searching...',
    'search.resultsFor': '{count} results for "{query}"',
    'search.noResults': 'No results found for "{query}". Try a different search term.',

    // SubsystemDetail
    'subsystem.apis': 'APIs',
    'subsystem.avgScore': 'Avg Score',
    'subsystem.coverage': 'Coverage',
    'subsystem.scoreDistribution': 'Score Distribution',
    'subsystem.effortDistribution': 'Effort Distribution',
    'subsystem.types': 'Types',
    'subsystem.topGaps': 'Top Gaps (Lowest Scores)',
    'subsystem.kind': 'Kind',
    'subsystem.package': 'Package',

    // Docs
    'docs.title': 'Documentation',
    'docs.codeReviews': 'Code Reviews',
    'docs.apiEnumeration': 'API Enumeration',
    'docs.androidApiEnum': 'API Enumeration (56,387 APIs)',
    'docs.ohApiEnum': 'API Enumeration',
    'docs.a2ohFactory': 'AI-Driven Shim Build Plans',
    'docs.a2ohFactoryDesc': 'Detailed plans for building the Android-to-OHOS API translation layer using AI-assisted code generation, automated testing, and headless ArkUI validation.',
    'docs.backToDocumentation': '← Back to Documentation',
    'docs.sourceRepo': 'Source',
    'docs.notFound': 'Document not found.',

    // Badge
    'badge.unknown': 'unknown',
  },
  zh: {
    // Header
    'siteName': 'Android → OH 门户',
    'nav.dashboard': '仪表盘',
    'nav.browse': '浏览',
    'nav.status': '项目状态',
    'nav.mappings': '映射',
    'nav.gaps': '差距',
    'nav.docs': '文档',
    'search.placeholder': '搜索 API...（如 Activity、startAbility、Canvas）',

    // DbLoader
    'status.init': '初始化中...',
    'status.wasm': '加载 SQL 引擎...',
    'status.downloading': '下载数据库（约 10 MB）...',
    'status.error': '数据库加载失败，请刷新页面。',
    'loader.title': 'Android → OpenHarmony 迁移门户',
    'loader.connecting': '连接中...',
    'loader.retry': '重试',

    // Dashboard
    'dashboard.title': 'Android ↔ OpenHarmony API 兼容性',
    'stat.androidApis': 'Android API 数',
    'stat.ohApis': 'OH API 数',
    'stat.mapped': '已映射',
    'stat.unmapped': '未映射',
    'stat.avgScore': '平均分数',
    'stat.totalMappings': '映射总数',
    'chart.scoreDistribution': '分数分布',
    'chart.migrationEffort': '迁移工作量',
    'table.coverageBySubsystem': '子系统覆盖率',
    'table.subsystem': '子系统',
    'table.apis': 'API 数',
    'table.wellMapped': '良好映射',
    'table.partial': '部分映射',
    'table.gaps': '差距',
    'table.avgScore': '平均分',
    'table.coverage': '覆盖率',
    'dashboard.compatSummary': '兼容性总结',
    'loading': '加载中...',

    // Browse
    'browse.androidPackages': 'Android 包',
    'browse.all': '全部',
    'browse.filterByName': '按名称筛选...',
    'browse.allScores': '所有分数',
    'browse.allEffort': '所有工作量',
    'browse.allTypes': '所有类型',
    'browse.results': '{count} 条结果',

    // Score filter options
    'score.8-10': '8-10（直接映射）',
    'score.5-7': '5-7（部分映射）',
    'score.3-4': '3-4（困难）',
    'score.1-2': '1-2（差距）',

    // Effort options
    'effort.trivial': '极简',
    'effort.easy': '简单',
    'effort.moderate': '中等',
    'effort.hard': '困难',
    'effort.rewrite': '重写',
    'effort.impossible': '不可行',

    // Type options
    'type.methods': '方法',
    'type.fields': '字段',
    'type.constructors': '构造函数',
    'type.enumConstants': '枚举常量',

    // Pagination
    'pagination.prev': '上一页',
    'pagination.next': '下一页',
    'pagination.pageOf': '第 {page} 页，共 {total} 页',

    // ApiDetail
    'apiDetail.class': '类',
    'apiDetail.signature': '签名',
    'apiDetail.returns': '返回值',
    'apiDetail.type': '类型',
    'apiDetail.deprecated': '已弃用',
    'apiDetail.mappingDetails': '映射详情',
    'apiDetail.score': '分数',
    'apiDetail.mappingType': '映射类型',
    'apiDetail.effort': '工作量',
    'apiDetail.flags': '标记',
    'apiDetail.paradigmShift': '范式转换',
    'apiDetail.uiRewrite': 'UI 重写',
    'apiDetail.gapAnalysis': '差距分析',
    'apiDetail.migrationGuide': '迁移指南',
    'apiDetail.alternativeMappings': '替代映射',
    'apiDetail.noEquivalent': '未找到 OpenHarmony 对应 API',

    // Mappings
    'mappings.title': 'API 映射',
    'mappings.searchPlaceholder': '搜索映射...',
    'mappings.androidApi': 'Android API',
    'mappings.ohApi': 'OH API',
    'mappings.type': '类型',
    'mappings.effort': '工作量',
    'mappings.flags': '标记',
    'mappings.noMapping': '无映射',
    'mappings.count': '{count} 条映射',
    'mappings.allTypes': '所有类型',
    'mappings.direct': '直接',
    'mappings.near': '近似',
    'mappings.partial': '部分',
    'mappings.composite': '组合',
    'mappings.none': '无',

    // Gaps
    'gaps.title': 'API 差距',
    'gaps.description': '兼容性分数 ≤ 3 的 Android API — 这些需要大量工作或没有 OpenHarmony 对应功能。',
    'gaps.filterPlaceholder': '筛选差距...',
    'gaps.count': '{count} 个差距',

    // Search
    'search.title': '搜索结果',
    'search.emptyPrompt': '在顶部搜索栏中输入查询以查找 API。',
    'search.searching': '搜索中...',
    'search.resultsFor': '"{query}" 的 {count} 条结果',
    'search.noResults': '未找到 "{query}" 的结果，请尝试其他搜索词。',

    // SubsystemDetail
    'subsystem.apis': 'API 数',
    'subsystem.avgScore': '平均分',
    'subsystem.coverage': '覆盖率',
    'subsystem.scoreDistribution': '分数分布',
    'subsystem.effortDistribution': '工作量分布',
    'subsystem.types': '类型',
    'subsystem.topGaps': '最大差距（最低分数）',
    'subsystem.kind': '种类',
    'subsystem.package': '包',

    // Docs
    'docs.title': '文档',
    'docs.codeReviews': '代码审查',
    'docs.apiEnumeration': 'API 枚举',
    'docs.androidApiEnum': 'API 枚举（56,387 个 API）',
    'docs.ohApiEnum': 'API 枚举',
    'docs.a2ohFactory': 'AI 驱动适配层构建计划',
    'docs.a2ohFactoryDesc': '基于 AI 辅助代码生成、自动化测试和无头 ArkUI 验证，构建 Android 到 OHOS API 翻译层的详细计划。',
    'docs.backToDocumentation': '← 返回文档',
    'docs.sourceRepo': '来源',
    'docs.notFound': '文档未找到。',

    // Badge
    'badge.unknown': '未知',
  },
};
