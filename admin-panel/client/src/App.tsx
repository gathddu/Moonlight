import { Route, Switch, Redirect } from "wouter";
import { ThemeProvider } from "./contexts/ThemeContext";
import Dashboard from "./pages/Dashboard";
import NodeDetail from "./pages/NodeDetail";
import SyncLogs from "./pages/SyncLogs";

function Router() {
  return (
    <Switch>
      <Route path={"/"}>
        <Redirect to="/dashboard" />
      </Route>
      <Route path={"/dashboard"} component={Dashboard} />
      <Route path={"/node/:id"} component={NodeDetail} />
      <Route path={"/nodes"} component={Dashboard} />
      <Route path={"/sync"} component={SyncLogs} />
      <Route path={"/logs"} component={SyncLogs} />
      <Route path={"/settings"}>
        <Dashboard />
      </Route>
    </Switch>
  );
}

function App() {
  return (
    <ThemeProvider defaultTheme="dark">
      <Router />
    </ThemeProvider>
  );
}

export default App;
