// art_compiler_stubs.cpp - debug operator<< stubs for libart-compiler link
#include <ostream>

namespace art {

enum class MemBarrierKind;
enum class MethodLoadKind;
enum class WriteBarrierKind;
enum class MethodCompilationStat;

class HLoadClass { public: enum class LoadKind; };
class HLoadString { public: enum class LoadKind; };
class Location { public: enum class Kind; enum class Policy; };

std::ostream& operator<<(std::ostream& os, MemBarrierKind)        { return os << "<MemBarrierKind>"; }
std::ostream& operator<<(std::ostream& os, MethodLoadKind)        { return os << "<MethodLoadKind>"; }
std::ostream& operator<<(std::ostream& os, WriteBarrierKind)      { return os << "<WriteBarrierKind>"; }
std::ostream& operator<<(std::ostream& os, MethodCompilationStat) { return os << "<MethodCompilationStat>"; }
std::ostream& operator<<(std::ostream& os, HLoadClass::LoadKind)  { return os << "<HLoadClass::LoadKind>"; }
std::ostream& operator<<(std::ostream& os, HLoadString::LoadKind) { return os << "<HLoadString::LoadKind>"; }
std::ostream& operator<<(std::ostream& os, Location::Kind)        { return os << "<Location::Kind>"; }
std::ostream& operator<<(std::ostream& os, Location::Policy)      { return os << "<Location::Policy>"; }

} // namespace art
