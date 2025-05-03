# Multi-threaded Dictionary Server (Socket & Thread-based)

## 📖 Overview

This project is a **multi-threaded client-server dictionary application** built in Java. It enables multiple clients to concurrently:
- Search for the meaning(s) of a word
- Add new words and meanings
- Remove existing words
- Append or update meanings for existing words

The system uses:
- **Sockets** (TCP) for reliable client-server communication
- **Threads** to handle concurrent client requests on the server side
- **JavaFX** or **Swing** for the graphical user interface (GUI)

---

## 🧱 Architecture

- **Client-Server Model**: One server handles multiple client connections using threads.
- **Communication Protocol**: Custom JSON-based message exchange via TCP sockets.
- **Concurrency**: `Thread-per-connection` model on the server.
- **Persistence**: Dictionary data is loaded from a file and updated in-memory.

---

## 🖥️ Functionalities

### ✅ Supported Client Operations:
1. **Query Meaning**  
   Input: `word`  
   Output: list of meanings or "Not found"

2. **Add New Word**  
   Input: `word`, `meaning(s)`  
   Output: "Success" or "Duplicate word"

3. **Remove Word**  
   Input: `word`  
   Output: "Success" or "Word not found"

4. **Add Meaning to Existing Word**  
   Input: `word`, `new meaning`  
   Output: "Success" or "Word not found"

5. **Update Existing Meaning**  
   Input: `word`, `old meaning`, `new meaning`  
   Output: "Success" or "Meaning not found"

All changes are synchronized across clients in real-time.

---

## 🚀 Getting Started

### 🖥️ Server Setup

```bash
java -jar DictionaryServer.jar <port> <dictionary-file>
