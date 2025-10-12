/**
 * 任务状态枚举
 */
export enum TaskStatus {
  UNCREATED = "UNCREATED",
  CREATED = "CREATED",
  RUNNING = "RUNNING",
  STOPPED = "STOPPED",
  FAILED = "FAILED",
  COMPLETED = "COMPLETED"
}

/**
 * 任务状态中文映射
 */
export const TaskStatusLabel: Record<TaskStatus, string> = {
  [TaskStatus.UNCREATED]: "未创建",
  [TaskStatus.CREATED]: "已创建,等待执行",
  [TaskStatus.RUNNING]: "执行中",
  [TaskStatus.STOPPED]: "已停止",
  [TaskStatus.FAILED]: "执行失败",
  [TaskStatus.COMPLETED]: "任务成功"
}

/**
 * 获取任务状态的中文标签
 * @param status - 任务状态枚举值
 * @returns 中文描述
 */
export function getTaskStatusLabel(status: TaskStatus): string {
  return TaskStatusLabel[status] || "未知状态"
}

/**
 * 是否显示插件配置
 * @param status - 任务状态枚举值
 * @returns 是否显示插件配置
 */
export function isShowResultSpec(status: TaskStatus): boolean {
  return true;
}

/**
 * 是否显示监测记录
 * @param status - 任务状态枚举值
 * @returns 是否显示插件配置
 */
export function isShowMonitorRecord(status: TaskStatus): boolean {
  return true;
}
