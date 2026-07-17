import { useEffect, useState } from "react";
import DashboardLayout from "@/components/DashboardLayout";
import { IPDS_URL, POLL_INTERVAL, LOG_MAX_ENTRIES } from "../config";

interface LogEntry {
  timestamp: string;
  level: string;
  node: string;
  message: string;
}

export default function SyncLogs() {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLogs = async () => {
      try {
        const response = await fetch(`${IPDS_URL}/api/nodes`);
        if (!response.ok) return;
        const nodes = await response.json();
        const now = new Date();
        const newEntries: LogEntry[] = nodes.map(
          (node: { nodeIdentifier: string; lastHeartbeat: string; status: string }) => ({
            timestamp: node.lastHeartbeat
              ? new Date(node.lastHeartbeat).toLocaleTimeString("en-US", { hour12: false })
              : now.toLocaleTimeString("en-US", { hour12: false }),
            level: "INFO",
            node: node.nodeIdentifier,
            message: `Heartbeat received — status: ${node.status}`,
          })
        );
        setLogs((prev) => [...newEntries, ...prev].slice(0, LOG_MAX_ENTRIES));
      } catch {
        // back-end unavailable, leave logs as-is
      } finally {
        setLoading(false);
      }
    };

    fetchLogs();
    const interval = setInterval(fetchLogs, POLL_INTERVAL);
    return () => clearInterval(interval);
  }, []);

  return (
    <DashboardLayout>
      <div className="flex flex-col h-full p-4 gap-4">
        <div className="flex items-center justify-between font-mono" style={{ fontSize: "11px" }}>
          <span style={{ color: "#28463c", letterSpacing: "1px" }}>
            SYNC_LOG_VIEWER.EXE
          </span>
          <span style={{ color: "#28463c" }}>
            ENTRIES: {logs.length}
          </span>
        </div>

        <div className="crt-monitor flex-1 flex flex-col">
          <div className="crt-screen flex-1" style={{ minHeight: "500px" }}>
            <div className="relative z-10 font-mono" style={{ fontSize: "11px" }}>
              {loading ? (
                <p className="py-4 animate-pulse" style={{ color: "#28463c" }}>
                  LOADING LOG DATA...
                </p>
              ) : logs.length === 0 ? (
                <p className="py-4" style={{ color: "#28463c" }}>
                  AWAITING BACKEND CONNECTION AT {IPDS_URL}...
                </p>
              ) : (
                logs.map((log, i) => (
                  <div key={i} className="flex gap-1" style={{ lineHeight: "1.8" }}>
                    <span style={{ color: "#28463c" }}>
                      [{log.timestamp}]
                    </span>
                    <span style={{ color: log.level === "WARN" ? "#8a7030" : "#37b455" }}>
                      [{log.level}]
                    </span>
                    <span style={{ color: "#43c863" }}>
                      {log.message} from {log.node}
                    </span>
                  </div>
                ))
              )}
            </div>
            <div
              style={{
                position: "absolute",
                top: "10px",
                right: "8px",
                width: "3px",
                height: "calc(100% - 20px)",
                background: "repeating-linear-gradient(180deg, #43c863 0px, #43c863 2px, transparent 2px, transparent 5px)",
                opacity: 0.4,
                zIndex: 10,
              }}
            />
            <div className="crt-progress" />
          </div>
          <CRTBottom />
        </div>
      </div>
    </DashboardLayout>
  );
}

function CRTBottom() {
  return (
    <div className="crt-bottom" style={{ justifyContent: "flex-end", gap: "6px", paddingTop: "6px" }}>
      <div className="crt-dots">
        {[...Array(5)].map((_, i) => (
          <div key={i} className="crt-dot" />
        ))}
        <div className="crt-dot active" />
      </div>
    </div>
  );
}
