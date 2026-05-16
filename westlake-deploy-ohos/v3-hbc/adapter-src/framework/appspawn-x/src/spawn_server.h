/**
 * appspawn-x spawn server.
 *
 * Listens on a Unix domain socket for spawn requests from foundation
 * (using the AppSpawnClient protocol). On each request, invokes the
 * registered SpawnHandler which typically fork()s a child process.
 */

#pragma once

#include "spawn_msg.h"
#include <functional>
#include <string>

namespace appspawnx {

// Callback type for handling spawn requests
// Returns child PID on success, -1 on failure
using SpawnHandler = std::function<int(const SpawnMsg&)>;

class SpawnServer {
public:
    explicit SpawnServer(const std::string& socketName);
    ~SpawnServer();

    // Initialize OH security modules
    int initSecurity(const std::string& sandboxConfigPath);

    // Create and bind listening socket
    int createListenSocket();

    // Set the spawn request handler
    void setSpawnHandler(SpawnHandler handler);

    // Enter main event loop (blocks forever)
    // Listens for connections, reads spawn requests, calls handler
    void run();

    // Close the listening socket (called in child after fork)
    void closeSocket();

private:
    std::string socketName_;
    std::string socketPath_;
    int listenFd_;
    SpawnHandler spawnHandler_;

    // Handle a single client connection
    void handleConnection(int clientFd);

    // Parse raw message buffer into SpawnMsg
    // In real implementation, this would use appspawn protocol parser
    bool parseMessage(const uint8_t* data, size_t len, SpawnMsg& msg);

    // Send spawn result back to client
    void sendResult(int clientFd, const SpawnResult& result);
};

} // namespace appspawnx
