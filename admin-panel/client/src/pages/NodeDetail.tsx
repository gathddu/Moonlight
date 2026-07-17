import { useEffect, useState } from "react";
import { useParams, Link } from "wouter";
import DashboardLayout from "@/components/DashboardLayout";
import { IPDS_URL, POLL_INTERVAL } from "../config";

interface Node {
  id: string;
  nodeIdentifier: string;
  ipAddress: string;
  port: number;
  status: string;
  role: string;
  uptimePercentage: number;
  totalUptime: number;
  storageCapacity: number;
  storageUsed: number;
  lastHeartbeat: string;
}

export default function NodeDetail() {
  const { id } = useParams<{ id: string }>();
  const [node, setNode] = useState<Node | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchNode = async () => {
      try {
        const response = await fetch(`${IPDS_URL}/api/nodes/${id}`);
        if (!response.ok) {
          setNode(null);
          return;
        }
        const data = await response.json();
        setNode(data);
      } catch {
        setNode(null);
      } finally {
        setLoading(false);
      }
    };

    fetchNode();
    const interval = setInterval(fetchNode, POLL_INTERVAL);
    return () => clearInterval(interval);
  }, [id]);

  const formatUptime = (seconds: number) => {
    const days = Math.floor(seconds / 86400);
    const hours = Math.floor((seconds % 86400) / 3600);
    const minutes = Math.floor((seconds % 3600) / 60);
    return `${days}d ${String(hours).padStart(2, "0")}h ${String(minutes).padStart(2, "0")}m`;
  };

  const formatBytes = (bytes: number) => {
    if (bytes === 0) return "0 B";
    const k = 1024;
    const sizes = ["B", "KB", "MB", "GB", "TB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round((bytes / Math.pow(k, i)) * 100) / 100 + " " + sizes[i];
  };

  return (
    <DashboardLayout>
      <div className="flex flex-col h-full p-4 gap-4">
        <div className="flex items-center justify-between font-mono" style={{ fontSize: "11px" }}>
          <span style={{ color: "#28463c", letterSpacing: "1px" }}>
            NODE_INSPECT.EXE — {id?.toUpperCase() || "UNKNOWN"}
          </span>
          <Link href="/dashboard" className="font-mono hover:underline" style={{ color: "#3a6070", fontSize: "11px" }}>
            ← BACK
          </Link>
        </div>

        <div className="crt-monitor flex-1 flex flex-col">
          <div className="crt-screen flex-1" style={{ minHeight: "400px" }}>
            <div className="relative z-10 font-mono">
              {loading ? (
                <p className="py-4 animate-pulse" style={{ color: "#28463c", fontSize: "12px" }}>
                  LOADING NODE DATA...
                </p>
              ) : !node ? (
                <p className="py-4" style={{ color: "#28463c", fontSize: "12px" }}>
                  BACKEND OFFLINE — NO DATA AVAILABLE
                </p>
              ) : (
                <div className="space-y-6">
                  <div className="text-center mt-2">
                    <h2 className="font-bold" style={{ color: "#43c863", fontSize: "20px", letterSpacing: "4px" }}>
                      {node.nodeIdentifier.toUpperCase()}
                      {node.role === "LEADER" && <span style={{ marginLeft: "10px", fontSize: "16px" }}>♛</span>}
                    </h2>
                    <div className="mt-1" style={{ color: "#28463c", fontSize: "9px" }}>
                      // NODE DETAILED INSPECTION
                    </div>
                  </div>

                  <div className="pt-4" style={{ fontSize: "12px", borderTop: "1px solid #0f1e24" }}>
                    <DataRow label="IDENTIFIER" value={node.nodeIdentifier} />
                    <DataRow label="STATUS" value={node.status} />
                    <DataRow label="ROLE" value={node.role} />
                    <DataRow label="IP ADDRESS" value={node.ipAddress} />
                    <DataRow label="PORT" value={String(node.port)} />
                    <DataRow label="UPTIME %" value={`${node.uptimePercentage.toFixed(2)}%`} />
                    <DataRow label="TOTAL UPTIME" value={formatUptime(node.totalUptime)} />
                    <DataRow label="STORAGE USED" value={formatBytes(node.storageUsed)} />
                    <DataRow label="STORAGE CAP" value={formatBytes(node.storageCapacity)} />
                    <DataRow
                      label="LAST HEARTBEAT"
                      value={
                        node.lastHeartbeat
                          ? new Date(node.lastHeartbeat).toLocaleString("en-US", { hour12: false })
                          : "NEVER"
                      }
                    />
                  </div>

                  <div className="pt-4" style={{ borderTop: "1px solid #0f1e24" }}>
                    <div className="mb-2" style={{ color: "#28463c", fontSize: "10px" }}>UPTIME VISUALIZATION:</div>
                    <div className="flex items-center gap-2">
                      <div className="flex-1 h-3" style={{ background: "#050d0f", border: "1px solid #0f1e24" }}>
                        <div
                          className="h-full"
                          style={{ width: `${node.uptimePercentage}%`, background: "#43c863" }}
                        />
                      </div>
                      <span style={{ color: "#43c863", fontSize: "11px" }}>
                        {node.uptimePercentage.toFixed(1)}%
                      </span>
                    </div>
                  </div>
                </div>
              )}
            </div>
            <div className="crt-progress" style={{ marginTop: "auto" }} />
          </div>
          <CRTBottom />
        </div>
      </div>
    </DashboardLayout>
  );
}

function DataRow({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex" style={{ lineHeight: "2.0" }}>
      <span style={{ color: "#43c863", width: "130px", flexShrink: 0 }}>{label}</span>
      <span style={{ color: "#43c863", width: "16px", textAlign: "center" }}>:</span>
      <span style={{ color: "#43c863" }}>{value}</span>
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