import { useEffect, useState } from "react";
import DashboardLayout from "@/components/DashboardLayout";
import { IPDS_URL, POLL_INTERVAL, LOG_MAX_ENTRIES } from "../config";

interface LogEntry {
  time: string;
  level: string;
  msg: string;
}

interface NodeData {
  name: string;
  role: string;
  isLeader: boolean;
  data: Record<string, string>;
}

export default function Dashboard() {
  const [logs, setLogs] = useState<LogEntry[]>([]);
  const [nodes, setNodes] = useState<NodeData[]>([]);
  const [lastUpdated, setLastUpdated] = useState("--:--:--");

  useEffect(() => {
    const fetchData = async () => {
      try {
        const res = await fetch(`${IPDS_URL}/api/nodes`);
        if (!res.ok) return;
        const rawNodes = await res.json();
        const now = new Date().toLocaleTimeString("en-US", { hour12: false });
        setLastUpdated(now);

        // map to node cards
        const mapped: NodeData[] = rawNodes.map((n: any) => ({
          name: (n.nodeIdentifier || n.id || "UNKNOWN").toUpperCase(),
          role: n.role || "UNKNOWN",
          isLeader: n.role === "LEADER",
          data: {
            STATUS: n.role || n.status || "UNKNOWN",
            UPTIME: n.uptimePercentage ? `${n.uptimePercentage.toFixed(1)}%` : "—",
            LOAD: n.load || "—",
            CPU: n.cpu || "—",
            MEM: n.mem || "—",
            IP: n.ipAddress || "—",
            REGION: n.region || "—",
          },
        }));
        setNodes(mapped);

        // log entries
        const entries: LogEntry[] = rawNodes.map((n: any) => ({
          time: now,
          level: "INFO",
          msg: `Heartbeat received from ${n.nodeIdentifier || n.id}`,
        }));
        setLogs((prev) => [...entries, ...prev].slice(0, LOG_MAX_ENTRIES));
      } catch {
        // offline, do nothing
      }
    };

    fetchData();
    const id = setInterval(fetchData, POLL_INTERVAL);
    return () => clearInterval(id);
  }, []);

  return (
    <DashboardLayout>
      <div className="flex flex-col h-full p-4 gap-4">
        {/* header */}
        <div className="flex items-center justify-between font-mono" style={{ fontSize: "11px" }}>
          <span style={{ color: "#28463c", letterSpacing: "1px" }}>SYSTEM_OVERVIEW.EXE</span>
          <div className="flex items-center gap-1">
            <span style={{ color: "#28463c", letterSpacing: "1px" }}>LAST UPDATED: {lastUpdated}</span>
            <span style={{ color: "#43c863" }} className="animate-pulse">▌</span>
          </div>
        </div>

        {/* CRT monitors, from live data or empty state */}
        {nodes.length === 0 ? (
          <div className="crt-monitor flex-1 flex flex-col">
            <div className="crt-screen flex-1 flex items-center justify-center">
              <p className="font-mono" style={{ color: "#28463c", fontSize: "12px" }}>
                AWAITING BACKEND CONNECTION AT {IPDS_URL}...
              </p>
            </div>
            <CRTBottom />
          </div>
        ) : (
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 flex-1 min-h-0">
            {nodes.map((node) => (
              <CRTMonitor key={node.name} {...node} />
            ))}
          </div>
        )}

        {/* log feed CRT */}
        <div className="crt-monitor" style={{ flex: "0 0 auto" }}>
          <div className="crt-screen" style={{ minHeight: "180px", position: "relative" }}>
            <div className="relative z-10 font-mono" style={{ fontSize: "11px" }}>
              {logs.length === 0 ? (
                <p style={{ color: "#28463c", lineHeight: "1.8" }}>NO LOG ENTRIES YET</p>
              ) : (
                logs.map((log, i) => (
                  <div key={i} className="flex gap-1" style={{ lineHeight: "1.8" }}>
                    <span style={{ color: "#28463c" }}>[{log.time}]</span>
                    <span style={{ color: "#37b455" }}>[{log.level}]</span>
                    <span style={{ color: "#43c863" }}>{log.msg}</span>
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

function CRTMonitor({
  name,
  isLeader,
  data,
}: {
  name: string;
  role: string;
  isLeader: boolean;
  data: Record<string, string>;
}) {
  return (
    <div className="crt-monitor flex flex-col">
      <div className="crt-screen flex-1">
        <div className="relative z-10 h-full flex flex-col">
          <div className="text-center mb-4 mt-2">
            <span
              className="font-mono font-bold"
              style={{ color: "#43c863", fontSize: "20px", letterSpacing: "4px" }}
            >
              {name}
              {isLeader && <span style={{ marginLeft: "10px", fontSize: "16px" }}>♛</span>}
            </span>
          </div>

          <div className="font-mono flex-1 flex flex-col justify-center" style={{ fontSize: "12px" }}>
            {Object.entries(data).map(([key, val]) => (
              <div key={key} className="flex" style={{ lineHeight: "2.0" }}>
                <span style={{ color: "#43c863", width: "80px", flexShrink: 0, textAlign: "left" }}>
                  {key}
                </span>
                <span style={{ color: "#43c863", width: "16px", textAlign: "center" }}>:</span>
                <span style={{ color: "#43c863" }}>{val}</span>
              </div>
            ))}
          </div>

          <div className="crt-progress" style={{ marginTop: "auto" }} />
        </div>
      </div>
      <CRTBottom />
    </div>
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
