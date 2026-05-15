// libbinder-port: minimal stub for android-base/logging.h required symbols.
// The real libbase logging.cpp depends on liblog daemon APIs (__android_log_*)
// that we don't ship.  We replace LogMessage with a stderr writer.

#include <stdarg.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <sstream>
#include <string>
#include <mutex>

#include <android-base/logging.h>

namespace android {
namespace base {

// LogMessageData: layout doesn't matter — only logging.h declares this as
// `class LogMessageData;` (forward declaration).  We supply a definition here.
class LogMessageData {
 public:
    std::ostringstream buffer;
    const char* file = nullptr;
    unsigned line = 0;
    LogSeverity severity = INFO;
    const char* tag = nullptr;
    int error = -1;
};

LogMessage::LogMessage(const char* file, unsigned line, LogId /*id*/,
                       LogSeverity severity, const char* tag, int error)
    : data_(new LogMessageData()) {
    data_->file = file;
    data_->line = line;
    data_->severity = severity;
    data_->tag = tag;
    data_->error = error;
}

LogMessage::LogMessage(const char* file, unsigned line, LogSeverity severity,
                       const char* tag, int error)
    : LogMessage(file, line, DEFAULT, severity, tag, error) {}

LogMessage::~LogMessage() {
    static std::mutex mu;
    std::lock_guard<std::mutex> lock(mu);
    const char* sev = "?";
    switch (data_->severity) {
        case VERBOSE: sev = "V"; break;
        case DEBUG:   sev = "D"; break;
        case INFO:    sev = "I"; break;
        case WARNING: sev = "W"; break;
        case ERROR:   sev = "E"; break;
        case FATAL_WITHOUT_ABORT:
        case FATAL:   sev = "F"; break;
        default: break;
    }
    fprintf(stderr, "%s %s: %s\n", sev, data_->tag ? data_->tag : "?",
            data_->buffer.str().c_str());
    if (data_->error != -1) {
        fprintf(stderr, "  (errno=%d: %s)\n", data_->error, strerror(data_->error));
    }
    if (data_->severity == FATAL) {
        ::abort();
    }
    // unique_ptr in data_ deletes LogMessageData itself.
}

std::ostream& LogMessage::stream() {
    return data_->buffer;
}

void LogMessage::LogLine(const char* /*file*/, unsigned /*line*/,
                         LogSeverity /*severity*/, const char* /*tag*/,
                         const char* /*msg*/) {}

// Severity controls.
static LogSeverity gMinimumLogSeverity = INFO;
LogSeverity GetMinimumLogSeverity() { return gMinimumLogSeverity; }
LogSeverity SetMinimumLogSeverity(LogSeverity new_severity) {
    LogSeverity old = gMinimumLogSeverity;
    gMinimumLogSeverity = new_severity;
    return old;
}
bool ShouldLog(LogSeverity severity, const char* /*tag*/) {
    return severity >= gMinimumLogSeverity;
}

ScopedLogSeverity::ScopedLogSeverity(LogSeverity new_severity)
    : old_(SetMinimumLogSeverity(new_severity)) {}
ScopedLogSeverity::~ScopedLogSeverity() {
    SetMinimumLogSeverity(old_);
}

void DefaultAborter(const char* /*abort_message*/) { ::abort(); }

void StderrLogger(LogId /*id*/, LogSeverity /*severity*/, const char* /*tag*/,
                  const char* /*file*/, unsigned /*line*/, const char* msg) {
    fprintf(stderr, "%s\n", msg);
}

void StdioLogger(LogId /*id*/, LogSeverity severity, const char* /*tag*/,
                 const char* /*file*/, unsigned /*line*/, const char* msg) {
    FILE* out = (severity >= ERROR) ? stderr : stdout;
    fprintf(out, "%s\n", msg);
}

void KernelLogger(LogId /*id*/, LogSeverity /*severity*/, const char* /*tag*/,
                  const char* /*file*/, unsigned /*line*/, const char* msg) {
    fprintf(stderr, "%s\n", msg);
}

LogFunction TeeLogger(LogFunction&& l1, LogFunction&& l2) {
    return [l1, l2](LogId id, LogSeverity sev, const char* tag, const char* file,
                    unsigned line, const char* msg) {
        l1(id, sev, tag, file, line, msg);
        l2(id, sev, tag, file, line, msg);
    };
}

void InitLogging(char* /*argv*/[], LogFunction&& /*logger*/,
                 AbortFunction&& /*aborter*/) {}

LogFunction SetLogger(LogFunction&& /*logger*/) { return StderrLogger; }
AbortFunction SetAborter(AbortFunction&& /*aborter*/) { return DefaultAborter; }

// LogdLogger no-op.
LogdLogger::LogdLogger(LogId default_log_id) : default_log_id_(default_log_id) {}
void LogdLogger::operator()(LogId, LogSeverity, const char* /*tag*/,
                            const char* /*file*/, unsigned int /*line*/,
                            const char* msg) {
    fprintf(stderr, "%s\n", msg);
}

void SetDefaultTag(const std::string& /*tag*/) {}

}  // namespace base
}  // namespace android
