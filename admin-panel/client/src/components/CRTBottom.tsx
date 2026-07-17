export function CRTBottom() {
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
