# Moonlight

admin panel for moonlight. crt terminals. green phosphor. lain watches over your nodes.

## Run It

```bash
nix develop
pnpm install
pnpm dev
```

opens at localhost:3000. needs node 20+ and pnpm.

## What It Does

connects to the IPDS back-end at localhost:8080. shows your nodes, their heartbeats, sync logs.

| path | what |
| ---- | ---- |
| /dashboard | two CRT monitors per node + heartbeat log feed |
| /node/:id | single node deep inspection |
| /sync | full-screen sync log viewer |
| /logs | same as sync |
| /settings | coming soon |

## Stack

react 18 ★ vite 7 ★ tailwind 4 ★ wouter ★ typescript

## Fonts

★ JetBrains Mono
★ UnifrakturMaguntia

## Colours

| what | hex |
| ---- | --- |
| void | #030809|
| phosphor green | #43c863 |
| sidebar steel | #5a7a8c |
| dim text | #28463c |
| nav active | 96dce8 |
