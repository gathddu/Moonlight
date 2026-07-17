import { ReactNode, useState, useEffect } from "react";
import { Link, useLocation } from "wouter";
import { cn } from "@/lib/utils";
import { Menu, X } from "lucide-react";

interface DashboardLayoutProps {
  children: ReactNode;
}

const LAIN_IMAGE = "/lain.jpg";

const navItems = [
  { path: "/dashboard", label: "OVERVIEW", icon: "✩₊˚.⋆☾⋆⁺₊✧" },
  { path: "/nodes", label: "NODES", icon: "⋆. ˖*༄*ੈ✩‧₊" },
  { path: "/sync", label: "SYNC", icon: ".˳·˖✶✶˖·˳." },
  { path: "/logs", label: "LOGS", icon: "˚. ✦.˳·˖✶ ⋆.✧̣̇˚." },
  { path: "/settings", label: "SETTINGS", icon: "( ᴗ͈ˬᴗ͈)ഒ" },
];

export default function DashboardLayout({ children }: DashboardLayoutProps) {
  const [location] = useLocation();
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [currentTime, setCurrentTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setCurrentTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  const formatTime = (d: Date) => d.toLocaleTimeString("en-US", { hour12: false });
  const formatDate = (d: Date) => {
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    const yyyy = d.getFullYear();
    return `${mm}/${dd}/${yyyy}`;
  };

  return (
    <div className="flex h-screen overflow-hidden" style={{ background: "#030809" }}>
      {sidebarOpen && (
        <div className="fixed inset-0 bg-black/80 z-40 lg:hidden" onClick={() => setSidebarOpen(false)} />
      )}

      {/* sidebar */}
      <aside
        className={cn(
          "fixed lg:static inset-y-0 left-0 z-50 transition-transform duration-300 lg:translate-x-0 flex flex-col relative overflow-hidden",
          sidebarOpen ? "translate-x-0" : "-translate-x-full"
        )}
        style={{
          width: "240px",
          minWidth: "240px",
          background: "#010507",
          borderRight: "1px solid #0a1518",
        }}
      >
        <button
          className="lg:hidden absolute top-3 right-3 z-30 p-1"
          style={{ color: "#7ab0c4", background: "none", border: "none", cursor: "pointer" }}
          onClick={() => setSidebarOpen(false)}
        >
          <X className="h-5 w-5" />
        </button>

        {/* lain watching over with scanlines overlay */}
        <div
          style={{
            position: "absolute",
            top: "18%",
            left: "5%",
            right: "5%",
            height: "38%",
            opacity: 0.4,
            backgroundImage: `url(${LAIN_IMAGE})`,
            backgroundSize: "cover",
            backgroundRepeat: "no-repeat",
            backgroundPosition: "center top",
            pointerEvents: "none",
            zIndex: 0,
            filter: "saturate(0.4) brightness(0.6)",
          }}
        />
        {/* scanlines overlay on lain */}
        <div
          style={{
            position: "absolute",
            top: "18%",
            left: "5%",
            right: "5%",
            height: "38%",
            pointerEvents: "none",
            zIndex: 1,
            background: "repeating-linear-gradient(0deg, transparent, transparent 2px, rgba(0,0,0,0.15) 2px, rgba(0,0,0,0.15) 4px)",
          }}
        />

        <div className="relative z-10 px-5 pt-4 pb-0">
          <div className="flex justify-center mb-1">
            <svg width="60" height="60" viewBox="0 0 60 60" fill="none">
              <path
                d="M30 5C18.954 5 10 13.954 10 25c0 11.046 8.954 20 20 20 3.5 0 6.783-.9 9.643-2.478C34.09 39.87 30 34.87 30 29c0-8.284 5.373-15.316 12.82-17.82C39.5 7.42 35 5 30 5z"
                fill="#3a5565"
              />
            </svg>
          </div>
          <h1
            className="font-gothic"
            style={{
              color: "#5a7a8c",
              fontSize: "44px",
              lineHeight: 1.0,
              marginTop: "0px",
            }}
          >
            Moonlight
          </h1>
          <div
            className="mt-1 font-mono leading-relaxed"
            style={{ fontSize: "8.5px", color: "#2a4050", letterSpacing: "0.5px" }}
          >
            <div>// DISTRIBUTED CLOUD SYSTEM</div>
            <div>// ADMIN PANEL</div>
          </div>
        </div>

        <nav className="relative z-10 px-2 flex-1" style={{ marginTop: "42%" }}>
          {navItems.map((item) => {
            const isActive = location === item.path || (item.path === "/dashboard" && location === "/");
            return (
              <Link
                key={item.path}
                href={item.path}
                className={cn(
                  "flex items-center gap-2 px-3 py-2 font-mono transition-all rounded-sm",
                  isActive && "nav-active"
                )}
                style={{
                  fontSize: "11px",
                  color: isActive ? "#96dce8" : "#3a6070",
                  letterSpacing: "0.5px",
                  textDecoration: "none",
                }}
              >
                <span style={{ fontSize: "10px", flexShrink: 0 }}>{item.icon}</span>
                <span style={{ fontWeight: isActive ? 600 : 400 }}>{item.label}</span>
              </Link>
            );
          })}
        </nav>

        <div className="relative z-10 mx-4 mb-2">
          <div
            className="p-3 font-mono flex"
            style={{
              border: "1px solid #0f1e24",
              background: "rgba(1, 5, 7, 0.95)",
              color: "#3a6070",
              fontSize: "9px",
              lineHeight: 1.8,
            }}
          >
            <div className="flex-1">
              <div>USER: ADMIN</div>
              <div>ROLE: SUPERUSER</div>
              <div>SESSION: 7f3a9c2e</div>
              <div>IP: 10.0.1:1</div>
              <div>TIME: {formatTime(currentTime)}</div>
              <div>DATE: {formatDate(currentTime)}</div>
            </div>
            <div
              style={{
                width: "32px",
                height: "32px",
                marginTop: "4px",
                opacity: 0.5,
                borderRadius: "50%",
                background: "radial-gradient(circle, #3a6070 30%, transparent 70%)",
                border: "1px solid #1a3040",
              }}
            />
          </div>
        </div>

        {/* quote */}
        <div className="relative z-10 px-4 pb-3 text-center">
          <p className="font-mono" style={{ fontSize: "9px", color: "#3a6070" }}>
            "THE WIRED IS WIDE."
          </p>
          <p className="font-mono" style={{ fontSize: "9px", color: "#253a48" }}>
            — LAIN
          </p>
        </div>
      </aside>

      {/* main */}
      <main className="flex-1 overflow-auto" style={{ background: "#030809" }}>
        <div className="sticky top-0 z-30 flex h-10 items-center gap-4 px-4 lg:hidden" style={{ background: "#010507", borderBottom: "1px solid #0a1518" }}>
          <button style={{ color: "#5a7a8c", background: "none", border: "none", cursor: "pointer" }} onClick={() => setSidebarOpen(true)}>
            <Menu className="h-5 w-5" />
          </button>
          <span className="font-gothic" style={{ color: "#5a7a8c", fontSize: "18px" }}>Moonlight</span>
        </div>
        <div className="h-full flex flex-col">{children}</div>
      </main>
    </div>
  );
}
