# Moonlight

## Summary
Moonlight is a high-availability, peer-to-peer distributed file synchronization and storage system that creates a "personal cloud" across a dynamic network of user-owned devices. The system employs a robust **Leader-Follower consensus model** orchestrated by a central **Internet Protocol Discovery Service (IPDS)**, ensuring data consistency and fault tolerance despite frequent node churn.

## Features
- Dynamic Leader-Follower Consensus: Automatic leader election based on uptime metrics for optimal network stability.
- High-Churn Resilience: Designed to handle frequent node connections and disconnections seamlessly.
- IPDS Orchestration: Central IPDS service manages node discovery, health monitoring and coordination.
- Efficient Synchronization: RSync-based delta file transfer minimizes bandwidth usage.
- Immutable Audit Trail: Log-based transactional control system ensures data integrity.
- Container-Ready: Full Docker and Kubernetes support for scalable deployment.
- Real-Time Monitoring: React-based admin panel for node status and system health.
