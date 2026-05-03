#!/bin/bash
# Compact acceptance/regression gate for real McD Westlake phone artifacts.
#
# Usage:
#   scripts/check-real-mcd-proof.sh [artifact-dir]
#
# With no artifact-dir, checks the latest artifacts/real-mcd/20* directory.

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
DEFAULT_ARTIFACT_ROOT="$REPO_ROOT/artifacts/real-mcd"
ARTIFACT="${1:-}"

if [ -z "$ARTIFACT" ]; then
    while IFS= read -r candidate; do
        if find "$candidate" -maxdepth 1 -type f -name 'logcat*.txt' | grep -q .; then
            ARTIFACT="$candidate"
        fi
    done < <(find "$DEFAULT_ARTIFACT_ROOT" -mindepth 1 -maxdepth 1 -type d -name '20*' | sort)
fi

if [ -z "$ARTIFACT" ] || [ ! -d "$ARTIFACT" ]; then
    echo "ERROR: artifact dir not found: ${ARTIFACT:-<empty>}" >&2
    exit 2
fi

LOG="$ARTIFACT/logcat.txt"
if [ ! -f "$LOG" ]; then
    LOG="$(find "$ARTIFACT" -maxdepth 1 -type f -name 'logcat*.txt' | sort | tail -1)"
fi
LOG_STREAM="$ARTIFACT/logcat-stream.txt"
LOG_DUMP="$ARTIFACT/logcat-dump.txt"
PROCESSES="$ARTIFACT/processes.txt"
SCREEN="$ARTIFACT/screen.png"
LOCAL_SHIM_HASH="$ARTIFACT/local_shim_hash.txt"
LOCAL_RUNTIME_HASHES="$ARTIFACT/local_runtime_hashes.txt"
PHONE_RUNTIME_HASHES="$ARTIFACT/phone_runtime_hashes.txt"
UNSAFE_FLAGS="$ARTIFACT/unsafe_flags.txt"

if [ -z "$LOG" ] || [ ! -f "$LOG" ]; then
    echo "ERROR: logcat file not found in $ARTIFACT" >&2
    exit 2
fi

count_re() {
    local file="$1"
    local pattern="$2"
    if [ -f "$file" ]; then
        grep -aE -c "$pattern" "$file" || true
    else
        echo 0
    fi
}

last_re() {
    local file="$1"
    local pattern="$2"
    if [ -f "$file" ]; then
        grep -aE "$pattern" "$file" | tail -1 || true
    fi
}

count_after_re() {
    local file="$1"
    local anchor="$2"
    local pattern="$3"
    if [ -f "$file" ]; then
        awk -v anchor="$anchor" -v pattern="$pattern" '
            $0 ~ anchor { seen = 1 }
            seen && $0 ~ pattern { count++ }
            END { print count + 0 }
        ' "$file"
    else
        echo 0
    fi
}

count_pdp_livedata_mutated() {
    local file="$1"
    if [ -f "$file" ]; then
        awk '
            /MCD_PDP_LIVEDATA_(MUTATION|COMMIT|ADD)/ { count++ }
            /MCD_PDP_LIVEDATA_STATE/ {
                if ($0 ~ /normalAdd=androidx\.lifecycle\.MutableLiveData_value_/ &&
                        $0 !~ /normalAdd=androidx\.lifecycle\.MutableLiveData_value_null_active_0_version_-1/) {
                    count++
                } else if ($0 ~ /editAdd=androidx\.lifecycle\.MutableLiveData_value_/ &&
                        $0 !~ /editAdd=androidx\.lifecycle\.MutableLiveData_value_null_active_0_version_-1/) {
                    count++
                } else if ($0 ~ /productLimit=androidx\.lifecycle\.MutableLiveData_value_/ &&
                        $0 !~ /productLimit=androidx\.lifecycle\.MutableLiveData_value_null_active_0_version_-1/) {
                    count++
                }
            }
            END { print count + 0 }
        ' "$file"
    else
        echo 0
    fi
}

extract_last_number_field() {
    local line="$1"
    local key="$2"
    printf '%s\n' "$line" \
        | grep -ao "${key}=[0-9][0-9]*" \
        | tail -1 \
        | cut -d= -f2 \
        || true
}

hash_for_path_suffix() {
    local file="$1"
    local suffix="$2"
    if [ -f "$file" ]; then
        awk -v suffix="$suffix" '{
            path=$2
            gsub(/\r/, "", path)
            if (length(path) >= length(suffix) &&
                substr(path, length(path) - length(suffix) + 1) == suffix) {
                print $1
            }
        }' "$file" | tail -1
    fi
}

status=0
artifact_base="$(basename "$ARTIFACT")"
pass() {
    echo "PASS $*"
}
warn() {
    echo "WARN $*"
}
fail() {
    echo "FAIL $*"
    status=1
}

echo "artifact=$ARTIFACT"
echo "log=$LOG"

unsafe_marker_count="$(count_re "$LOG" 'unsafe_|unsafe-|UNSAFE|unsafe_observer_dispatch_opt_in')"
unsafe_flag_count=0
if [ -f "$UNSAFE_FLAGS" ]; then
    unsafe_flag_count="$(count_re "$UNSAFE_FLAGS" 'unsafe_probe=1|westlake_mcd_unsafe_.*=(1|true|TRUE|yes|YES|enabled|ENABLED)')"
fi
unsafe_name_count=0
case "$artifact_base" in
    *no_unsafe*|*NO_UNSAFE*|*unsafe_off*|*UNSAFE_OFF*) unsafe_name_count=0 ;;
    *true_unsafe*|*TRUE_UNSAFE*|*unsafe_probe*|*UNSAFE_PROBE*|*unsafe_cart_commit*|*UNSAFE_CART_COMMIT*|*unsafe_model*|*UNSAFE_MODEL*|*unsafe_storage*|*UNSAFE_STORAGE*|*unsafe_observer*|*UNSAFE_OBSERVER*) unsafe_name_count=1 ;;
    *unsafe*|*UNSAFE*) unsafe_name_count=1 ;;
esac
unsafe_probe=0
if [ "$unsafe_marker_count" -gt 0 ] || [ "$unsafe_flag_count" -gt 0 ] || [ "$unsafe_name_count" -gt 0 ]; then
    unsafe_probe=1
fi
if [ "$unsafe_probe" -eq 0 ]; then
    pass "proof_unsafe_flags_off markers=0 flags=0 artifact=$artifact_base"
else
    fail "proof_unsafe_flags_off unsafeProbe=1 markers=$unsafe_marker_count flags=$unsafe_flag_count artifact=$artifact_base"
    last_re "$LOG" 'unsafe_|unsafe-|UNSAFE|unsafe_observer_dispatch_opt_in'
fi

if [ -s "$LOG_STREAM" ]; then
    stream_bytes="$(stat -c%s "$LOG_STREAM")"
    if cmp -s "$LOG_STREAM" "$LOG"; then
        pass "proof_logcat_streamed source=logcat-stream.txt bytes=$stream_bytes"
    else
        fail "proof_logcat_streamed source_mismatch stream_bytes=$stream_bytes log_bytes=$(stat -c%s "$LOG" 2>/dev/null || echo 0)"
    fi
else
    fail "proof_logcat_streamed missing_or_empty path=$LOG_STREAM"
fi
if [ -s "$LOG_DUMP" ]; then
    pass "proof_logcat_dump_present bytes=$(stat -c%s "$LOG_DUMP")"
else
    warn "proof_logcat_dump_present missing_or_empty path=$LOG_DUMP"
fi

current_shim_hash=""
if [ -f "$REPO_ROOT/aosp-shim.dex" ]; then
    current_shim_hash="$(sha256sum "$REPO_ROOT/aosp-shim.dex" | awk '{print $1}')"
fi
artifact_shim_hash="$(hash_for_path_suffix "$LOCAL_SHIM_HASH" 'aosp-shim.dex')"
if [ -n "$current_shim_hash" ] && [ "$artifact_shim_hash" = "$current_shim_hash" ]; then
    pass "proof_current_local_shim_hash hash=$artifact_shim_hash"
else
    fail "proof_current_local_shim_hash artifact=${artifact_shim_hash:-missing} current=${current_shim_hash:-missing}"
fi
if [ -s "$LOCAL_RUNTIME_HASHES" ]; then
    local_runtime_shim_hash="$(hash_for_path_suffix "$LOCAL_RUNTIME_HASHES" 'aosp-shim.dex')"
    if [ "$local_runtime_shim_hash" = "$current_shim_hash" ]; then
        pass "proof_local_runtime_hashes_present aosp_shim=$local_runtime_shim_hash"
    else
        fail "proof_local_runtime_hashes_present aosp_shim=${local_runtime_shim_hash:-missing} current=${current_shim_hash:-missing}"
    fi
else
    fail "proof_local_runtime_hashes_present missing path=$LOCAL_RUNTIME_HASHES legacy_local_shim_hash=${artifact_shim_hash:-missing}"
fi
if [ -s "$PHONE_RUNTIME_HASHES" ]; then
    phone_runtime_shim_hash="$(hash_for_path_suffix "$PHONE_RUNTIME_HASHES" 'aosp-shim.dex')"
    if [ "$phone_runtime_shim_hash" = "$current_shim_hash" ]; then
        pass "proof_phone_runtime_hashes_current aosp_shim=$phone_runtime_shim_hash"
    else
        fail "proof_phone_runtime_hashes_current aosp_shim=${phone_runtime_shim_hash:-missing} current=${current_shim_hash:-missing}"
    fi
else
    fail "proof_phone_runtime_hashes_current missing path=$PHONE_RUNTIME_HASHES"
fi

if [ -f "$SCREEN" ]; then
    screen_sha="$(sha256sum "$SCREEN" | awk '{print $1}')"
    screen_bytes="$(stat -c%s "$SCREEN")"
    echo "screen_sha=$screen_sha"
    echo "screen_bytes=$screen_bytes"
else
    fail "screenshot_missing path=$SCREEN"
fi

host_pid=""
vm_pid=""
vm_ppid=""
subprocess_purity_ok=0
real_mcd_guest_ok=0
if [ -f "$PROCESSES" ]; then
    host_pid="$(awk '{sub(/\r$/,"")} $4=="com.westlake.host"{print $2; exit}' "$PROCESSES")"
    vm_pid="$(awk '{sub(/\r$/,"")} $4=="dalvikvm"{print $2; exit}' "$PROCESSES")"
    vm_ppid="$(awk '{sub(/\r$/,"")} $4=="dalvikvm"{print $3; exit}' "$PROCESSES")"
    direct_mcd="$(count_re "$PROCESSES" '(^|[[:space:]])com\.mcdonalds\.app($|[[:space:]])')"
    real_mcd_guest_line="$(awk '
        {
            sub(/\r$/, "")
            if ($4 == "dalvikvm" &&
                    $0 ~ /-Dwestlake\.apk\.package=com\.mcdonalds\.app/ &&
                    $0 ~ /-Dwestlake\.apk\.path=\/data\/local\/tmp\/westlake\/com_mcdonalds_app\.apk/ &&
                    $0 ~ /com_mcdonalds_app_classes\.dex/) {
                print
                exit
            }
        }
    ' "$PROCESSES" || true)"
    mock_guest_line="$(awk '
        {
            sub(/\r$/, "")
            if ($4 == "dalvikvm" &&
                    $0 ~ /test-apps|controlled-showcase|cutoff|yelp|material|mock/) {
                print
                exit
            }
        }
    ' "$PROCESSES" || true)"
    if [ -n "$host_pid" ] && [ -n "$vm_pid" ] && [ "$vm_ppid" = "$host_pid" ] && [ "$direct_mcd" -eq 0 ]; then
        subprocess_purity_ok=1
        pass "westlake_subprocess_purity host_pid=$host_pid vm_pid=$vm_pid vm_ppid=$vm_ppid direct_mcd_processes=0"
    else
        fail "westlake_subprocess_purity host_pid=${host_pid:-missing} vm_pid=${vm_pid:-missing} vm_ppid=${vm_ppid:-missing} direct_mcd_processes=$direct_mcd"
    fi
    if [ -n "$real_mcd_guest_line" ] && [ -z "$mock_guest_line" ]; then
        real_mcd_guest_ok=1
        pass "proof_real_mcd_guest_dalvikvm package=com.mcdonalds.app apk=com_mcdonalds_app.apk classes=com_mcdonalds_app_classes.dex"
    else
        fail "proof_real_mcd_guest_dalvikvm real_guest=$([ -n "$real_mcd_guest_line" ] && echo 1 || echo 0) mock_guest=$([ -n "$mock_guest_line" ] && echo 1 || echo 0) vm_pid=${vm_pid:-missing}"
        if [ -n "$mock_guest_line" ]; then
            echo "mock_guest_process=$mock_guest_line"
        elif [ -n "$real_mcd_guest_line" ]; then
            echo "real_guest_process=$real_mcd_guest_line"
        fi
    fi
else
    fail "process_snapshot_missing path=$PROCESSES"
fi

fatal_re='Fatal signal|SIGBUS|BUS_ADRALN|SIGSEGV|SIGABRT|FATAL EXCEPTION|JNI DETECTED ERROR|Failed requirement|Application\.onCreate error summary|McD onCreate failure'
fatal_count="$(count_re "$LOG" "$fatal_re")"
if [ "$fatal_count" -eq 0 ]; then
    pass "no_fatal_failed_requirement count=0"
else
    fail "no_fatal_failed_requirement count=$fatal_count"
    last_re "$LOG" "$fatal_re"
fi

dashboard_count="$(count_re "$LOG" 'Dashboard active: com\.mcdonalds\.homedashboard\.activity\.HomeDashboardActivity')"
root_count="$(count_re "$LOG" 'PF-MCD-ROOT phase=select activity=com\.mcdonalds\.homedashboard\.activity\.HomeDashboardActivity')"
if [ "$dashboard_count" -gt 0 ] && [ "$root_count" -gt 0 ]; then
    pass "dashboard_active count=$dashboard_count root_select=$root_count"
elif [ "$root_count" -gt 0 ]; then
    pass "dashboard_root_selected dashboard_active_marker=$dashboard_count root_select=$root_count"
else
    fail "dashboard_active count=$dashboard_count root_select=$root_count"
fi

nr_passthrough="$(count_re "$LOG" 'NewRelic URLConnection passthrough')"
nr_url_noop="$(count_re "$LOG" 'NewRelic noop java\.net\.URLConnection com\.newrelic\.agent\.android\.instrumentation\.URLConnectionInstrumentation\.openConnection')"
westlake_http_bridge="$(count_re "$LOG" 'PFCUT-MCD-NET] bridge response status=2[0-9][0-9]|PFCUT-MCD-NET shadow (bridge|client) response status=2[0-9][0-9]|WestlakeHttp.*(success|response|code=2)')"
if { [ "$nr_passthrough" -gt 0 ] || [ "$westlake_http_bridge" -gt 0 ]; } && [ "$nr_url_noop" -eq 0 ]; then
    pass "network_bridge_or_urlconnection newrelic=$nr_passthrough westlake_bridge=$westlake_http_bridge urlconnection_noop=0"
else
    fail "network_bridge_or_urlconnection newrelic=$nr_passthrough westlake_bridge=$westlake_http_bridge urlconnection_noop=$nr_url_noop"
    last_re "$LOG" 'NewRelic (URLConnection passthrough|noop java\.net\.URLConnection com\.newrelic\.agent\.android\.instrumentation\.URLConnectionInstrumentation\.openConnection)|PFCUT-MCD-NET] bridge response'
fi

network_attempts="$(count_re "$LOG" 'NewRelic URLConnection|URLConnectionInstrumentation|HttpURLConnection|McDHttpClient|MWBaseRequest|getUrl\(\)|WestlakeHttp|HTTP/|PFCUT-MCD-NET] (provider|bridge response)|PFCUT-MCD-NET shadow')"
network_successes="$(count_re "$LOG" 'HTTP/[0-9.]+ 2[0-9][0-9]|responseCode[ =]2[0-9][0-9]|status[ =]2[0-9][0-9]|WestlakeHttp.*(success|response|code=2)|PFCUT-MCD-NET] bridge response status=2[0-9][0-9]|PFCUT-MCD-NET shadow (bridge|client) response status=2[0-9][0-9]')"
network_errors="$(count_re "$LOG" 'UnknownHostException|ConnectException|SocketTimeoutException|SSLHandshakeException|SSLPeerUnverifiedException|bad HTTP status|HTTP/[0-9.]+ [45][0-9][0-9]|HTTP [45][0-9][0-9]|IOException|\[NPE\].*(HttpURLConnection|MWBaseRequest|EndPointSetting|OrderingConfiguration)')"
http_npe="$(count_re "$LOG" '\[NPE\] void java\.net\.HttpURLConnection\.setRequestMethod\(java\.lang\.String\)')"
echo "network_attempt_markers=$network_attempts"
echo "network_success_markers=$network_successes"
echo "network_error_markers=$network_errors"
if [ "$http_npe" -eq 0 ]; then
    pass "http_urlconnection_no_setrequestmethod_npe count=0"
else
    fail "http_urlconnection_no_setrequestmethod_npe count=$http_npe"
    last_re "$LOG" 'HttpURLConnection\.setRequestMethod\(java\.lang\.String\)'
fi

endpoint_npe="$(count_re "$LOG" '\[NPE\].*EndPointSetting\.getPath\(\)')"
ordering_npe="$(count_re "$LOG" '\[NPE\].*OrderingConfiguration\.getCartResponseTTL\(\)')"
if [ "$endpoint_npe" -eq 0 ]; then
    pass "endpointsetting_no_npe count=0"
else
    fail "endpointsetting_no_npe count=$endpoint_npe"
    last_re "$LOG" '\[NPE\].*EndPointSetting\.getPath\(\)'
fi
if [ "$ordering_npe" -eq 0 ]; then
    pass "orderingconfiguration_no_npe count=0"
else
    fail "orderingconfiguration_no_npe count=$ordering_npe"
    last_re "$LOG" '\[NPE\].*OrderingConfiguration\.getCartResponseTTL\(\)'
fi

stock_view_attached="$(count_re "$LOG" 'MCD_DASH_STOCK_VIEW_ATTACHED .*fragment=android\.widget\.ScrollView.*sections=android\.widget\.LinearLayout#0x7f0b16c5')"
u6_seeded="$(count_re "$LOG" 'MCD_DASH_U6_SEEDED placeholders=HERO:0x6f,MENU:0x71,PROMOTION:0x74,POPULAR:0x7c')"
sections_ready="$(count_re "$LOG" 'MCD_DASH_SECTIONS_READY hero=true menu=true promotion=true popular=true count=4')"
real_hero_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]fragment_home_dashboard_hero_section(_updated)? resource=0x7f0e028[23] root=RelativeLayout')"
real_menu_guest_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]home_menu_guest_user resource=0x7f0e0366 root=LinearLayout')"
real_promotion_section_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]fragment_promotion_section resource=0x7f0e030e root=RelativeLayout')"
real_popular_section_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]fragment_popular_section resource=0x7f0e0305 root=RelativeLayout')"
real_promotion_item_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]home_promotion_item resource=0x7f0e036a root=LinearLayout')"
real_popular_item_xml="$(count_re "$LOG" 'MCD_REAL_XML_INFLATED layout=layout[/_]home_popular_item_adapter resource=0x7f0e0369 root=LinearLayout')"
root_fallback_false="$(count_re "$LOG" 'PF-MCD-ROOT phase=select activity=com\.mcdonalds\.homedashboard\.activity\.HomeDashboardActivity .*fallback=false')"
promotion_adapter_bounds="$(count_re "$LOG" 'MCD_DASH_RV_BOUNDS adapter=com\.mcdonalds\.homedashboard\.adapter\.HomePromotionAdapter count=[1-9]')"
popular_adapter_bounds="$(count_re "$LOG" 'MCD_DASH_RV_BOUNDS adapter=com\.mcdonalds\.homedashboard\.adapter\.HomePopularItemsAdapter count=[1-9]')"
frame_count="$(count_re "$LOG" 'Strict dashboard frame')"
frame_line="$(last_re "$LOG" 'Strict dashboard frame')"
frame_bytes="$(extract_last_number_field "$frame_line" bytes)"
frame_views="$(extract_last_number_field "$frame_line" views)"
frame_texts="$(extract_last_number_field "$frame_line" texts)"
frame_buttons="$(extract_last_number_field "$frame_line" buttons)"
frame_images="$(extract_last_number_field "$frame_line" images)"
frame_rows="$(extract_last_number_field "$frame_line" rows)"
projection_ready=0
if [ "$root_count" -gt 0 ] && [ "$root_fallback_false" -gt 0 ] \
        && [ "$real_promotion_item_xml" -gt 0 ] && [ "$real_popular_item_xml" -gt 0 ] \
        && [ "$promotion_adapter_bounds" -gt 0 ] && [ "$popular_adapter_bounds" -gt 0 ] \
        && [ "$frame_count" -gt 0 ] && [ "${frame_bytes:-0}" -ge 200 ] \
        && [ "${frame_views:-0}" -ge 20 ] && [ "${frame_rows:-0}" -ge 4 ]; then
    projection_ready=1
    pass "mcd_stock_dashboard_projection root_select=$root_count fallback=false promo_item_xml=$real_promotion_item_xml popular_item_xml=$real_popular_item_xml promo_adapter=$promotion_adapter_bounds popular_adapter=$popular_adapter_bounds strict_frames=$frame_count rows=${frame_rows:-0}"
else
    warn "mcd_stock_dashboard_projection_incomplete root_select=$root_count fallback_false=$root_fallback_false promo_item_xml=$real_promotion_item_xml popular_item_xml=$real_popular_item_xml promo_adapter=$promotion_adapter_bounds popular_adapter=$popular_adapter_bounds strict_frames=$frame_count rows=${frame_rows:-0}"
fi
if [ "$stock_view_attached" -gt 0 ]; then
    pass "mcd_stock_dashboard_view_attached count=$stock_view_attached"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_stock_dashboard_view_attached count=0 tier=projection"
else
    fail "mcd_stock_dashboard_view_attached count=0"
fi
if [ "$u6_seeded" -gt 0 ]; then
    pass "mcd_u6_section_placeholders_seeded count=$u6_seeded"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_u6_section_placeholders_seeded count=0 tier=projection"
else
    fail "mcd_u6_section_placeholders_seeded count=0"
fi
if [ "$sections_ready" -gt 0 ]; then
    pass "mcd_sections_ready count=$sections_ready"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_sections_ready count=0 tier=projection"
else
    fail "mcd_sections_ready count=0"
fi
if [ "$real_hero_xml" -gt 0 ]; then
    pass "mcd_real_hero_xml_inflated count=$real_hero_xml"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_real_hero_xml_inflated count=0 tier=projection"
else
    fail "mcd_real_hero_xml_inflated count=0"
fi
if [ "$real_menu_guest_xml" -gt 0 ]; then
    pass "mcd_real_menu_guest_xml_inflated count=$real_menu_guest_xml"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_real_menu_guest_xml_inflated count=0 tier=projection"
else
    fail "mcd_real_menu_guest_xml_inflated count=0"
fi
if [ "$real_promotion_section_xml" -gt 0 ]; then
    pass "mcd_real_promotion_section_xml_inflated count=$real_promotion_section_xml"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_real_promotion_section_xml_inflated count=0 tier=projection item_xml=$real_promotion_item_xml"
else
    fail "mcd_real_promotion_section_xml_inflated count=0"
fi
if [ "$real_popular_section_xml" -gt 0 ]; then
    pass "mcd_real_popular_section_xml_inflated count=$real_popular_section_xml"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_real_popular_section_xml_inflated count=0 tier=projection item_xml=$real_popular_item_xml"
else
    fail "mcd_real_popular_section_xml_inflated count=0"
fi
if [ "$real_promotion_item_xml" -gt 0 ]; then
    pass "mcd_real_promotion_item_xml_inflated count=$real_promotion_item_xml"
else
    warn "mcd_real_promotion_item_xml_inflated count=0"
fi
if [ "$real_popular_item_xml" -gt 0 ]; then
    pass "mcd_real_popular_item_xml_inflated count=$real_popular_item_xml"
else
    warn "mcd_real_popular_item_xml_inflated count=0"
fi

real_child_sections=0
missing_real_child_sections=""
declare -A expected_child_patterns=(
    [HERO]='MCD_DASH_REAL_VIEW_ATTACHED section=HERO id=0x6f fragment=com\.mcdonalds\.homedashboard\.fragment\.HomeHeroFragment .*dashboard=com\.mcdonalds\.homedashboard\.fragment\.HomeDashboardFragment'
    [MENU]='MCD_DASH_REAL_VIEW_ATTACHED section=MENU id=0x71 fragment=com\.mcdonalds\.homedashboard\.fragment\.guestuser\.HomeMenuGuestUserFragment .*dashboard=com\.mcdonalds\.homedashboard\.fragment\.HomeDashboardFragment'
    [PROMOTION]='MCD_DASH_REAL_VIEW_ATTACHED section=PROMOTION id=0x74 fragment=com\.mcdonalds\.homedashboard\.fragment\.HomePromotionFragment .*dashboard=com\.mcdonalds\.homedashboard\.fragment\.HomeDashboardFragment'
    [POPULAR]='MCD_DASH_REAL_VIEW_ATTACHED section=POPULAR id=0x7c fragment=com\.mcdonalds\.homedashboard\.fragment\.HomePopularFragment .*dashboard=com\.mcdonalds\.homedashboard\.fragment\.HomeDashboardFragment'
)
for section in HERO MENU PROMOTION POPULAR; do
    section_count="$(count_re "$LOG" "${expected_child_patterns[$section]}")"
    if [ "$section_count" -gt 0 ]; then
        real_child_sections=$((real_child_sections + 1))
        if [ "$section_count" -gt 1 ]; then
            warn "mcd_real_child_fragment_view_duplicate section=$section count=$section_count"
        fi
    else
        missing_real_child_sections="${missing_real_child_sections}${missing_real_child_sections:+,}$section"
    fi
done
fallback_section_attached="$(count_re "$LOG" 'MCD_DASH_SECTION_VIEW_ATTACHED')"
fallback_or_failure="$(count_re "$LOG" 'Dashboard fallback UI populated|MCD_DASH_FALLBACK|MCD_DASH_XML_SHELL_RESULT|MCD_DASH_SECTION_VIEW_ATTACH_FAIL|MCD_DASH_REAL_VIEW_ATTACH_FAIL|MCD_DASH_REAL_VIEW_EMPTY|MCD_REAL_XML_INFLATE_FAILED layout=layout_home_menu_guest_user|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_home_menu_guest_user|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e0366|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e0366|MCD_REAL_XML_INFLATE_FAILED layout=layout_fragment_promotion_section|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_fragment_promotion_section|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e030e|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e030e|MCD_REAL_XML_INFLATE_FAILED layout=layout_fragment_popular_section|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_fragment_popular_section|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e0305|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e0305')"
if [ "$real_child_sections" -eq 4 ]; then
    pass "mcd_real_child_fragment_views sections=4"
elif [ "$projection_ready" -eq 1 ]; then
    warn "mcd_real_child_fragment_views sections=$real_child_sections missing=${missing_real_child_sections:-none} tier=projection"
else
    fail "mcd_real_child_fragment_views sections=$real_child_sections missing=${missing_real_child_sections:-none}"
fi
if [ "$fallback_section_attached" -eq 0 ]; then
    pass "mcd_no_section_layout_fallback_attached count=0"
else
    fail "mcd_no_section_layout_fallback_attached count=$fallback_section_attached"
    last_re "$LOG" 'MCD_DASH_SECTION_VIEW_ATTACHED'
fi
if [ "$fallback_or_failure" -eq 0 ]; then
    pass "mcd_no_dashboard_fallback_or_real_view_failure count=0"
else
    fail "mcd_no_dashboard_fallback_or_real_view_failure count=$fallback_or_failure"
    last_re "$LOG" 'Dashboard fallback UI populated|MCD_DASH_FALLBACK|MCD_DASH_XML_SHELL_RESULT|MCD_DASH_SECTION_VIEW_ATTACH_FAIL|MCD_DASH_REAL_VIEW_ATTACH_FAIL|MCD_DASH_REAL_VIEW_EMPTY|MCD_REAL_XML_INFLATE_FAILED layout=layout_home_menu_guest_user|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_home_menu_guest_user|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e0366|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e0366|MCD_REAL_XML_INFLATE_FAILED layout=layout_fragment_promotion_section|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_fragment_promotion_section|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e030e|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e030e|MCD_REAL_XML_INFLATE_FAILED layout=layout_fragment_popular_section|MCD_REAL_XML_INFLATE_FALLBACK layout=layout_fragment_popular_section|LAYOUT_INFLATER_AXML_PARSE_ERROR resource=0x7f0e0305|LAYOUT_INFLATER_AXML_NON_BINARY resource=0x7f0e0305'
fi

if [ "$frame_count" -gt 0 ]; then
    echo "strict_frame_last=$frame_line"
    echo "strict_frame_count=$frame_count bytes=${frame_bytes:-?} views=${frame_views:-?} texts=${frame_texts:-?} buttons=${frame_buttons:-?} images=${frame_images:-?} rows=${frame_rows:-?}"
    pass "strict_dashboard_frame_present count=$frame_count"
    if [ "${frame_bytes:-0}" -lt 200 ] || [ "${frame_views:-0}" -lt 20 ]; then
        fail "strict_dashboard_frame_sparse bytes=${frame_bytes:-0} views=${frame_views:-0}"
    elif [ "${frame_texts:-0}" -eq 0 ] && [ "${frame_buttons:-0}" -eq 0 ] \
            && [ "${frame_images:-0}" -eq 0 ] && [ "${frame_rows:-0}" -eq 0 ]; then
        fail "strict_dashboard_frame_empty_content texts=${frame_texts:-0} buttons=${frame_buttons:-0} images=${frame_images:-0} rows=${frame_rows:-0}"
    fi
else
    fail "strict_dashboard_frame_present count=0"
fi

pdp_activity="$(count_re "$LOG" 'OrderProductDetailsActivity|MCD_PDP_FRAGMENT_|MCD_ORDER_PDP_|MCD_PDP_CART_GATE')"
pdp_view_created="$(count_re "$LOG" 'MCD_PDP_FRAGMENT_VIEW_CREATED')"
pdp_view_attached="$(count_re "$LOG" 'MCD_PDP_FRAGMENT_VIEW_ATTACHED')"
pdp_resumed="$(count_re "$LOG" 'MCD_PDP_FRAGMENT_RESUMED|MCD_PDP_LIVEDATA_STATE .*fragmentState=7 .*fragmentResumed=true|MCD_PDP_LIVEDATA_STATE .*lifecycleState=RESUMED')"
pdp_semantic_popular_click="$(count_re "$LOG" 'MCD_DASH_SEMANTIC_POPULAR_CLICK .*HomePopularItemsAdapter .*handled=true')"
pdp_xml_enhanced="$(count_re "$LOG" 'MCD_REAL_XML_PDP_ENHANCED .*productInfo=[1-9][0-9]* .*scroll=ScrollView .*normalized=true')"
pdp_include_ok="$(count_re "$LOG" 'MCD_PDP_INCLUDE_BINDING .* ok=true')"
pdp_field_hydrate_ok="$(count_re "$LOG" 'MCD_PDP_FIELD_HYDRATE .*plusOk=true .*minusOk=true .*quantityOk=true')"
pdp_soft_resume_recovery="$(count_re "$LOG" 'MCD_PDP_FRAGMENT_RESUME_RECOVERY .*mode=soft_state')"
pdp_ready_true="$(count_re "$LOG" 'MCD_ORDER_PDP_READY .*ready=true')"
pdp_ready_false="$(count_re "$LOG" 'MCD_ORDER_PDP_READY .*ready=false')"
pdp_deps_missing="$(count_re "$LOG" 'MCD_ORDER_PDP_STOCK_ACTION .*route=fragment_deps_missing')"
pdp_j8="$(count_re "$LOG" 'MCD_ORDER_PDP_STOCK_ACTION .*control=add_to_order .*route=fragment_j8(_deferred|_enter)? .*invoked=true')"
pdp_post_timeout="$(count_re "$LOG" 'MCD_ORDER_PDP_DEFERRED_STOCK_ADD .*phase=post_timeout')"
pdp_stock_binding_listener="$(count_re "$LOG" 'MCD_PDP_STOCK_BINDING_PREP .*listenerInstalled=true')"
pdp_stock_livedata_seed="$(count_re "$LOG" 'MCD_PDP_STOCK_LIVEDATA_PREP .*after=true')"
pdp_justflip_seed="$(count_re "$LOG" 'MCD_JUSTFLIP_BASKET_FLAG_SEED .*maxQtyAfter=[1-9][0-9]* .*maxItemAfter=[1-9][0-9]*')"
pdp_justflip_seed_line="$(last_re "$LOG" 'MCD_JUSTFLIP_BASKET_FLAG_SEED')"
pdp_telemetry_seed="$(count_re "$LOG" 'MCD_TELEMETRY_MANAGER_SEED')"
pdp_telemetry_seed_line="$(last_re "$LOG" 'MCD_TELEMETRY_MANAGER_SEED')"
pdp_basket_a1="$(count_re "$LOG" 'BasketAPIHandler\.A1\(')"
pdp_telemetry_abort_after_basket_a1="$(count_after_re "$LOG" 'BasketAPIHandler[.]A1[(]' 'Telemetry_not_initialized')"
pdp_stock_view_click="$(count_re "$LOG" 'MCD_PDP_STOCK_VIEW_CLICK .*route=performClick invoked=true')"
pdp_stock_view_touch="$(count_re "$LOG" 'WESTLAKE_VIEW_TOUCH_LIFECYCLE .*id=0x7f0b1291')"
pdp_stock_click_boolean_npe="$(count_re "$LOG" '\[NPE\].*(Boolean\.booleanValue|OrderPdpButtonLayoutBindingImpl)')"
pdp_livedata_state="$(count_re "$LOG" 'MCD_PDP_LIVEDATA_STATE')"
pdp_observer_dispatch_gate="$(count_re "$LOG" 'MCD_PDP_OBSERVER_DISPATCH_GATE')"
pdp_observer_dispatch_blocked="$(count_re "$LOG" 'MCD_PDP_OBSERVER_DISPATCH_GATE .*allowed=false')"
pdp_observer_dispatch_invoked="$(count_re "$LOG" 'MCD_PDP_OBSERVER_DISPATCH .*invoked=true')"
pdp_cart_gate="$(count_re "$LOG" 'MCD_PDP_CART_GATE')"
pdp_cart_gate_before="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*phase=fragment_j8_before')"
pdp_cart_gate_after="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*phase=fragment_j8_after')"
pdp_cart_info_null="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*cartInfo=null')"
pdp_cart_product_zero="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*quantity=0 .*maxQtty=0')"
pdp_cart_product_hydrated="$(count_re "$LOG" 'MCD_PDP_CART_PRODUCT_PREP .*quantitySet=true .*afterQuantity=[1-9][0-9]*|MCD_PDP_CART_GATE .*quantity=[1-9][0-9]*')"
pdp_cart_product_max_zero="$(count_re "$LOG" 'MCD_PDP_CART_PRODUCT_PREP .*maxQtty=0|MCD_PDP_CART_GATE .*maxQtty=0')"
pdp_cart_product_choices_npe="$(count_re "$LOG" '\[NPE\].*CartProduct\.getChoices\(\)|CartProduct\.getChoices\(\)|ProductUseCase\.s\(CartProduct\)|ProductUseCase\.s\(com\.mcdonalds\.androidsdk\.ordering\.network\.model\.basket\.CartProduct\)')"
pdp_cart_product_choices_hydrated="$(count_re "$LOG" 'MCD_PDP_CART_PRODUCT_(PREP|HYDRATE|STATE|CHOICES).*choices(Ok|Set|Hydrated|NonNull|Ready)?=true|MCD_PDP_CART_PRODUCT_(PREP|HYDRATE|STATE|CHOICES).*choices(Size|Count)=[0-9][0-9]*|MCD_PDP_CART_GATE .*choices(Size|Count)=[0-9][0-9]*|MCD_PDP_REALMLIST_HYDRATE .*model=.*CartProduct.*seen=[1-9][0-9]* .*present=[1-9][0-9]*')"
pdp_cart_mutated="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*phase=fragment_j8_after .*(cartSizeWithoutOffers=[1-9][0-9]*|totalBagCount=[1-9][0-9]*)')"
pdp_cartinfo_readback_positive="$(count_re "$LOG" 'MCD_PDP_CARTINFO_READBACK .*(stockTotalBagCount=[1-9][0-9]*|stockCartProductQuantity=[1-9][0-9]*)')"
pdp_cartinfo_bridge_positive="$(count_re "$LOG" 'MCD_PDP_CARTINFO_SET_BRIDGE .*afterVmTotalBagCount=[1-9][0-9]*')"
pdp_cart_or_bag_mutated="$(count_re "$LOG" 'MCD_PDP_CART_GATE .*(cartSizeWithoutOffers=[1-9][0-9]*|totalBagCount=[1-9][0-9]*)|MCD_PDP_CARTINFO_SET_BRIDGE .*afterVmTotalBagCount=[1-9][0-9]*|MCD_(CART|BAG)_(COUNT|MUTATION|STATE|UPDATE).*(count|bag|size|total)[^0-9]*[1-9][0-9]*')"
pdp_livedata_mutated="$(count_pdp_livedata_mutated "$LOG")"
pdp_exact_stock_rejection="$(count_re "$LOG" 'MCD_(PDP_|ORDER_PDP_|CART_|BAG_)?(EXACT_)?STOCK_REJECT(ION|ED)?|MCD_STOCK_ADD_REJECT(ION|ED)?|exact stock rejection|stock rejection')"
pdp_basket_commit_entry="$(count_re "$LOG" 'BasketAPIHandler\.[A-Za-z0-9_$]+\(|OrderPDPFragment\.A7\(|OrderPDPViewModel\.X|BasketUseCase|BasketDataRepository|OrderingManager')"
pdp_downstream_sigbus="$(count_re "$LOG" 'Fatal signal 7 \(SIGBUS\)|SIGBUS|BUS_ADRALN')"
pdp_realm_native="$(count_re "$LOG" 'PFCUT-REALM|io\.realm\.internal|RootStorageCommonManager|RealmQuery')"
pdp_realm_storage_blocker=0
if [ "$pdp_downstream_sigbus" -gt 0 ] && [ "$pdp_realm_native" -gt 0 ]; then
    pdp_realm_storage_blocker=1
fi
pdp_projected_stock_hit="$(count_re "$LOG" 'MCD_ORDER_PDP_PROJECTED_HIT .*control=add_to_order .*stockClick=true .*handled=true')"
pdp_generic_add_input="$(count_re "$LOG" 'GENERIC_HIT_CLICK .*add_to_order|MCD_ORDER_PDP_STOCK_ACTION .*input=(generic|physical)|MCD_ORDER_PDP_GENERIC_HIT .*control=add_to_order .*handled=true|MCD_ORDER_PDP_GENERIC_ROUTE .*handled=true|MCD_PDP_STOCK_VIEW_CLICK source=(generic_hit|physical|generic) .*invoked=true|WESTLAKE_VIEW_TOUCH_LIFECYCLE .*id=0x7f0b1291')"
pdp_basecart_age_lt="$(count_re "$LOG" 'basecart-active-query .*predicate=_maxAge <')"
pdp_basecart_age_ne="$(count_re "$LOG" 'basecart-active-query .*predicate=_maxAge !=')"
pdp_basecart_status="$(count_re "$LOG" 'basecart-active-query .*predicate=cartStatus =')"
pdp_basecart_result="$(count_re "$LOG" 'result-size .*size=[1-9][0-9]*|query-find .*row=[1-9][0-9]*|table-row table=class_BaseCart row=[1-9][0-9]*')"
pdp_frame_count="$(count_re "$LOG" 'Strict McD order PDP frame')"
pdp_frame_line="$(last_re "$LOG" 'Strict McD order PDP frame')"
pdp_frame_bytes="$(extract_last_number_field "$pdp_frame_line" bytes)"
pdp_frame_views="$(extract_last_number_field "$pdp_frame_line" views)"
pdp_frame_texts="$(extract_last_number_field "$pdp_frame_line" texts)"
pdp_frame_images="$(extract_last_number_field "$pdp_frame_line" images)"
if [ "$pdp_activity" -gt 0 ]; then
    echo "pdp_activity_markers=$pdp_activity"
    pdp_add_cart_substatus=0
    telemetry_green=0
    generic_input_green=0
    lifecycle_green=0
    cart_or_stock_resolution_green=0
    cartproduct_lists_green=0
    if [ "$pdp_semantic_popular_click" -gt 0 ]; then
        pass "mcd_pdp_dashboard_popular_click count=$pdp_semantic_popular_click"
    else
        warn "mcd_pdp_dashboard_popular_click count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_xml_enhanced" -gt 0 ]; then
        pass "mcd_pdp_real_xml_enhanced count=$pdp_xml_enhanced"
    else
        warn "mcd_pdp_real_xml_enhanced count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_frame_count" -gt 0 ]; then
        echo "pdp_strict_frame_last=$pdp_frame_line"
        echo "pdp_strict_frame_count=$pdp_frame_count bytes=${pdp_frame_bytes:-?} views=${pdp_frame_views:-?} texts=${pdp_frame_texts:-?} images=${pdp_frame_images:-?}"
        if [ "${pdp_frame_bytes:-0}" -ge 200 ] && [ "${pdp_frame_views:-0}" -ge 20 ]; then
            pass "mcd_pdp_strict_frame_present count=$pdp_frame_count"
        else
            warn "mcd_pdp_strict_frame_sparse bytes=${pdp_frame_bytes:-0} views=${pdp_frame_views:-0}"
            pdp_add_cart_substatus=1
        fi
    else
        warn "mcd_pdp_strict_frame_present count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_view_created" -gt 0 ]; then
        pass "mcd_pdp_fragment_view_created count=$pdp_view_created"
    else
        warn "mcd_pdp_fragment_view_created count=0 substatus_uses_current_markers=true"
    fi
    if [ "$pdp_view_attached" -gt 0 ]; then
        pass "mcd_pdp_fragment_view_attached count=$pdp_view_attached"
    else
        warn "mcd_pdp_fragment_view_attached count=0 substatus_uses_current_markers=true"
    fi
    if [ "$pdp_resumed" -gt 0 ]; then
        pass "mcd_pdp_fragment_resumed count=$pdp_resumed"
    else
        warn "mcd_pdp_fragment_resumed count=0 soft_resume_recovery=$pdp_soft_resume_recovery"
    fi
    if [ "$pdp_include_ok" -gt 0 ]; then
        pass "mcd_pdp_include_binding_ok count=$pdp_include_ok"
    else
        warn "mcd_pdp_include_binding_ok count=0"
    fi
    if [ "$pdp_field_hydrate_ok" -gt 0 ]; then
        pass "mcd_pdp_field_hydrate_ok count=$pdp_field_hydrate_ok"
    else
        warn "mcd_pdp_field_hydrate_ok count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_ready_true" -gt 0 ]; then
        pass "mcd_pdp_ready_true count=$pdp_ready_true false=$pdp_ready_false"
    else
        warn "mcd_pdp_ready_true count=0 false=$pdp_ready_false deps_missing=$pdp_deps_missing"
        last_re "$LOG" 'MCD_ORDER_PDP_READY|MCD_ORDER_PDP_STOCK_ACTION .*fragment_deps_missing'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_stock_binding_listener" -gt 0 ]; then
        pass "mcd_pdp_stock_binding_listener_installed count=$pdp_stock_binding_listener"
    else
        warn "mcd_pdp_stock_binding_listener_installed count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_stock_livedata_seed" -gt 0 ]; then
        pass "mcd_pdp_stock_livedata_seeded count=$pdp_stock_livedata_seed"
    else
        warn "mcd_pdp_stock_livedata_seeded count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_justflip_seed" -gt 0 ]; then
        pass "pf626_justflip_basket_config_seeded count=$pdp_justflip_seed"
        echo "pf626_justflip_last=$pdp_justflip_seed_line"
    else
        fail "pf626_justflip_basket_config_seeded count=0 next_gap=seed_justflip_basket_limits"
        last_re "$LOG" 'MCD_JUSTFLIP_BASKET_FLAG_SEED|JustFlip'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_telemetry_seed" -gt 0 ]; then
        pass "mcd_pdp_telemetry_manager_seeded count=$pdp_telemetry_seed"
        echo "mcd_pdp_telemetry_seed_last=$pdp_telemetry_seed_line"
    else
        fail "mcd_pdp_telemetry_manager_seeded count=0 next_gap=seed_telemetry_manager_before_basket_commit"
        last_re "$LOG" 'MCD_TELEMETRY_MANAGER_SEED|Telemetry'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_stock_click_boolean_npe" -eq 0 ]; then
        pass "mcd_pdp_stock_click_boolean_npe count=0"
    else
        fail "mcd_pdp_stock_click_boolean_npe count=$pdp_stock_click_boolean_npe"
        last_re "$LOG" '\[NPE\].*(Boolean\.booleanValue|OrderPdpButtonLayoutBindingImpl)'
        pdp_add_cart_substatus=1
    fi
    if { [ "$pdp_j8" -gt 0 ] || [ "$pdp_stock_view_click" -gt 0 ]; } && [ "$pdp_post_timeout" -eq 0 ]; then
        pass "mcd_pdp_stock_add_entry j8=$pdp_j8 stockViewClick=$pdp_stock_view_click stockTouch=$pdp_stock_view_touch post_timeout=0"
    else
        warn "mcd_pdp_stock_add_entry j8=$pdp_j8 stockViewClick=$pdp_stock_view_click stockTouch=$pdp_stock_view_touch post_timeout=$pdp_post_timeout"
        last_re "$LOG" 'MCD_ORDER_PDP_STOCK_ACTION .*control=add_to_order|MCD_PDP_STOCK_VIEW_CLICK|MCD_ORDER_PDP_DEFERRED_STOCK_ADD'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_livedata_state" -gt 0 ]; then
        pass "mcd_pdp_livedata_state_probe count=$pdp_livedata_state"
        last_re "$LOG" 'MCD_PDP_LIVEDATA_STATE'
    elif [ "$pdp_stock_view_click" -gt 0 ]; then
        warn "mcd_pdp_livedata_state_probe count=0 next_gap=add_stock_view_continuation_probe"
    fi
    if [ "$pdp_observer_dispatch_gate" -gt 0 ]; then
        pass "mcd_pdp_observer_dispatch_gate count=$pdp_observer_dispatch_gate blocked=$pdp_observer_dispatch_blocked invoked=$pdp_observer_dispatch_invoked"
        last_re "$LOG" 'MCD_PDP_OBSERVER_DISPATCH_GATE|MCD_PDP_OBSERVER_DISPATCH '
    elif [ "$pdp_stock_view_click" -gt 0 ]; then
        warn "mcd_pdp_observer_dispatch_gate count=0 next_gap=observer_or_lifecycle_dispatch"
    fi
    if [ "$pdp_cart_gate" -gt 0 ]; then
        if [ "$pdp_cart_info_null" -eq 0 ]; then
            pass "mcd_pdp_cart_gate cart_gate=$pdp_cart_gate before=$pdp_cart_gate_before after=$pdp_cart_gate_after cartInfoNull=0"
        else
            warn "mcd_pdp_cart_gate cart_gate=$pdp_cart_gate cartInfoNull=$pdp_cart_info_null next_gap=seed_or_fetch_empty_cart"
            last_re "$LOG" 'MCD_PDP_CART_GATE'
            pdp_add_cart_substatus=1
        fi
    else
        warn "mcd_pdp_cart_gate count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_cart_product_hydrated" -gt 0 ]; then
        pass "mcd_pdp_cart_product_hydrated count=$pdp_cart_product_hydrated maxQttyZero=$pdp_cart_product_max_zero"
    else
        warn "mcd_pdp_cart_product_hydrated count=0"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_cart_product_choices_npe" -gt 0 ]; then
        fail "mcd_pdp_cartproduct_choices_hydrated choicesNullNpe=$pdp_cart_product_choices_npe hydratedMarkers=$pdp_cart_product_choices_hydrated next_gap=seed_cartproduct_choices_list"
        last_re "$LOG" 'CartProduct\.getChoices\(\)|ProductUseCase\.s\(com\.mcdonalds\.androidsdk\.ordering\.network\.model\.basket\.CartProduct\)'
        pdp_add_cart_substatus=1
    elif [ "$pdp_cart_product_choices_hydrated" -gt 0 ]; then
        cartproduct_lists_green=1
        pass "mcd_pdp_cartproduct_choices_hydrated markers=$pdp_cart_product_choices_hydrated choicesNullNpe=0"
    else
        fail "mcd_pdp_cartproduct_choices_hydrated markers=0 choicesNullNpe=0 next_gap=instrument_or_hydrate_cartproduct_choices_list"
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_basecart_age_lt" -gt 0 ] && [ "$pdp_basecart_age_ne" -gt 0 ] \
            && [ "$pdp_basecart_status" -gt 0 ] && [ "$pdp_basecart_result" -gt 0 ]; then
        pass "mcd_basecart_active_queries ageLt=$pdp_basecart_age_lt ageNe=$pdp_basecart_age_ne status=$pdp_basecart_status result=$pdp_basecart_result"
    else
        warn "mcd_basecart_active_queries ageLt=$pdp_basecart_age_lt ageNe=$pdp_basecart_age_ne status=$pdp_basecart_status result=$pdp_basecart_result next_gap=basecart_active_predicates_or_observer"
    fi
    if [ "$pdp_basket_commit_entry" -gt 0 ]; then
        pass "mcd_pdp_downstream_basket_commit_reached count=$pdp_basket_commit_entry"
    else
        warn "mcd_pdp_downstream_basket_commit_reached count=0"
    fi
    if [ "$pdp_basket_a1" -gt 0 ] && [ "$pdp_telemetry_abort_after_basket_a1" -eq 0 ]; then
        if [ "$pdp_telemetry_seed" -gt 0 ]; then
            telemetry_green=1
        fi
        pass "mcd_pdp_no_telemetry_abort_after_basket_a1 basketA1=$pdp_basket_a1 telemetryAbortAfter=0"
    elif [ "$pdp_basket_a1" -gt 0 ]; then
        fail "mcd_pdp_no_telemetry_abort_after_basket_a1 basketA1=$pdp_basket_a1 telemetryAbortAfter=$pdp_telemetry_abort_after_basket_a1"
        last_re "$LOG" 'BasketAPIHandler\.A1\(|Telemetry_not_initialized'
        pdp_add_cart_substatus=1
    else
        warn "mcd_pdp_no_telemetry_abort_after_basket_a1 basketA1=0 telemetryAbortAfter=$pdp_telemetry_abort_after_basket_a1"
    fi
    if [ "$pdp_cart_or_bag_mutated" -gt 0 ]; then
        pass "mcd_pdp_cart_or_bag_mutation_status mutated=$pdp_cart_or_bag_mutated"
    else
        warn "mcd_pdp_cart_or_bag_mutation_status mutated=0 cartGate=$pdp_cart_gate"
        last_re "$LOG" 'MCD_PDP_CART_GATE|MCD_(CART|BAG)_(COUNT|MUTATION|STATE|UPDATE)'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_cartinfo_readback_positive" -gt 0 ] || [ "$pdp_cartinfo_bridge_positive" -gt 0 ]; then
        pass "mcd_pdp_cartinfo_projection_status readback=$pdp_cartinfo_readback_positive bridge=$pdp_cartinfo_bridge_positive"
    else
        warn "mcd_pdp_cartinfo_projection_status readback=0 bridge=0"
        last_re "$LOG" 'MCD_PDP_CARTINFO_READBACK|MCD_PDP_CARTINFO_SET_BRIDGE'
    fi
    if [ "$pdp_livedata_mutated" -gt 0 ]; then
        pass "mcd_pdp_livedata_mutation_status mutated=$pdp_livedata_mutated"
    else
        warn "mcd_pdp_livedata_mutation_status mutated=0 stateProbe=$pdp_livedata_state"
        last_re "$LOG" 'MCD_PDP_LIVEDATA_(STATE|MUTATION|COMMIT|ADD)'
        pdp_add_cart_substatus=1
    fi
    if [ "$pdp_realm_storage_blocker" -eq 1 ]; then
        warn "pf626_current_downstream_blocker type=realm_sigbus realmNative=$pdp_realm_native sigbus=$pdp_downstream_sigbus"
        last_re "$LOG" 'PFCUT-REALM|io\.realm\.internal|RootStorageCommonManager|Fatal signal 7 \(SIGBUS\)|SIGBUS|BUS_ADRALN'
    else
        pass "pf626_current_downstream_blocker type=none realmNative=$pdp_realm_native sigbus=$pdp_downstream_sigbus"
    fi
    if [ "$pdp_downstream_sigbus" -gt 0 ]; then
        warn "mcd_pdp_downstream_basket_commit_crash sigbus=$pdp_downstream_sigbus"
        last_re "$LOG" 'BasketAPIHandler\.[A-Za-z0-9_$]+|Fatal signal 7 \(SIGBUS\)|SIGBUS|BUS_ADRALN'
    else
        pass "mcd_pdp_downstream_basket_commit_crash sigbus=0"
    fi
    if [ "$pdp_projected_stock_hit" -gt 0 ]; then
        pass "mcd_pdp_projected_stock_hit count=$pdp_projected_stock_hit"
    else
        warn "mcd_pdp_projected_stock_hit count=0"
    fi
    if [ "$pdp_add_cart_substatus" -eq 0 ]; then
        echo "pdp_add_cart_gate_status=PASS"
    else
        echo "pdp_add_cart_gate_status=FAIL"
    fi
    if [ "$pdp_resumed" -eq 0 ]; then
        fail "mcd_full_app_lifecycle_gate soft_resume_recovery=$pdp_soft_resume_recovery fragment_resumed=$pdp_resumed"
    else
        lifecycle_green=1
        pass "mcd_full_app_lifecycle_gate soft_resume_recovery=$pdp_soft_resume_recovery fragment_resumed=$pdp_resumed"
    fi
    if { [ "$pdp_cart_or_bag_mutated" -gt 0 ] && [ "$pdp_cart_product_zero" -eq 0 ]; } \
            || [ "$pdp_exact_stock_rejection" -gt 0 ]; then
        cart_or_stock_resolution_green=1
        pass "mcd_full_app_cart_mutation_gate cartOrBagMutated=$pdp_cart_or_bag_mutated exactStockRejection=$pdp_exact_stock_rejection cartProductZero=$pdp_cart_product_zero"
    else
        fail "mcd_full_app_cart_mutation_gate cartOrBagMutated=$pdp_cart_or_bag_mutated exactStockRejection=$pdp_exact_stock_rejection cartProductZero=$pdp_cart_product_zero cartProductHydrated=$pdp_cart_product_hydrated maxQttyZero=$pdp_cart_product_max_zero basketCommit=$pdp_basket_commit_entry realmStorageBlocker=$pdp_realm_storage_blocker sigbus=$pdp_downstream_sigbus next_gap=downstream_basket_commit_or_exact_stock_rejection"
        last_re "$LOG" 'MCD_PDP_CART_GATE|MCD_(PDP_|ORDER_PDP_|CART_|BAG_)?(EXACT_)?STOCK_REJECT(ION|ED)?|MCD_STOCK_ADD_REJECT(ION|ED)?|exact stock rejection|stock rejection'
    fi
    if [ "$pdp_generic_add_input" -gt 0 ]; then
        generic_input_green=1
        pass "mcd_full_app_generic_pdp_input_gate count=$pdp_generic_add_input"
    else
        fail "mcd_full_app_generic_pdp_input_gate count=0 next_gap=physical_or_generic_add_tap"
    fi
    if [ "$unsafe_probe" -eq 0 ] && [ "$subprocess_purity_ok" -eq 1 ] \
            && [ "$real_mcd_guest_ok" -eq 1 ] && [ "$cartproduct_lists_green" -eq 1 ] \
            && [ "$telemetry_green" -eq 1 ] && [ "$generic_input_green" -eq 1 ] \
            && [ "$lifecycle_green" -eq 1 ] && [ "$cart_or_stock_resolution_green" -eq 1 ]; then
        pass "pf621_final_acceptance_gate unsafeOff=1 subprocess=1 guestDalvikvm=1 telemetry=1 genericInput=1 lifecycle=1 cartProductLists=1 cartOrStockResolution=1"
    else
        fail "pf621_final_acceptance_gate unsafeOff=$((1 - unsafe_probe)) subprocess=$subprocess_purity_ok guestDalvikvm=$real_mcd_guest_ok telemetry=$telemetry_green genericInput=$generic_input_green lifecycle=$lifecycle_green cartProductLists=$cartproduct_lists_green cartOrStockResolution=$cart_or_stock_resolution_green"
    fi
fi

if [ "$status" -eq 0 ]; then
    echo "gate_status=PASS"
else
    echo "gate_status=FAIL"
fi

exit "$status"
