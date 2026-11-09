const { contextBridge, ipcRenderer } = require("electron");

contextBridge.exposeInMainWorld("workspaceApi", {
  draft: (payload) => ipcRenderer.invoke("workspace:draft", payload),
  assign: (vacancyId, payload) => ipcRenderer.invoke("workspace:assign", vacancyId, payload),
  complete: (vacancyId, holdId) => ipcRenderer.invoke("workspace:complete", vacancyId, holdId),
  cancel: (vacancyId, holdId) => ipcRenderer.invoke("workspace:cancel", vacancyId, holdId),
  overview: () => ipcRenderer.invoke("workspace:overview")
});
