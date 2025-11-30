<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { VPageHeader, VCard, IconDashboard } from '@halo-dev/components'
import { axiosInstance } from '@halo-dev/api-client'
import { TaskStatus, getTaskStatusLabel, isShowResult, isShowNextScheduledExecution } from '../enum/TaskStatus'
import BooleanIcon from '@/components/BooleanIcon.vue'

// 模拟从接口获取的任务状态（默认未创建）
const taskStatus = ref<TaskStatus>(TaskStatus.UNCREATED)

// 任务状态
interface Status {
  taskStatus: string
  cronExpression: string
  lastScheduledExecution: string
  lastActualExecution: string
  lastCompletionExecution: string
  lastCompletionTime: string
  nextScheduledExecution: string
  remainingTime: string
}
// 任务状态初始化
const status = ref<Status>({
  taskStatus: taskStatus.value,
  cronExpression: '-',
  lastScheduledExecution: '-',
  lastActualExecution: '-',
  lastCompletionExecution: '-',
  lastCompletionTime: '-',
  nextScheduledExecution: '-',
  remainingTime: '-',
})

// 获取任务状态
const fetchTaskStatus = async () => {
  axiosInstance.get('/apis/result.linkshealthmonitor.tch.cool/v1alpha1/status').then((res) => {
    const data = res.data
    // 任务状态
    taskStatus.value = data.taskStatus
    // 更新页面数据
    status.value = {
      taskStatus: data.taskStatus,
      cronExpression: data.cronExpression,
      lastScheduledExecution: data.lastScheduledExecution ?? '-',
      lastActualExecution: data.lastActualExecution ?? '-',
      lastCompletionExecution: data.lastCompletionExecution ?? '-',
      lastCompletionTime: data.lastCompletionTime ?? '-',
      nextScheduledExecution: isShowNextScheduledExecution(data.taskStatus) ? (data.nextScheduledExecution ?? '-') : '-',
      remainingTime: data.remainingTime ?? '-',
    }

    // 获取友链监测结果
    if (isShowResult(data.taskStatus)) {
      fetchMonitorResult()
    } else {
      // 友链监测记录显示无数据
    }
  })
}

// 插件配置
interface Config {
  customizedCronEnable: string
  customizedCron: string
  customizedCronAvailable: string
  externalUrl: string
}
// 插件配置初始化
const config = ref<Config>({
  customizedCronEnable: '-',
  customizedCron: '-',
  customizedCronAvailable: '-',
  externalUrl: '-',
})

// 友链监测记录
interface Record {
  no: number
  // 网站地址
  linkUrl: string
  // 网站名称
  linkDisplayName: string
  // 网站logo
  linkLogo: string
  // 友链分组
  linkGroupDisplayName: string

  // 功能监测
  // 网站是否可以访问
  websiteAccessible: boolean
  // 网站logo是否可以访问
  logoAccessible: boolean
  // 网站名称是否有变更
  displayNameChanged: boolean
  // 网站的最新名称
  latestDisplayName: string
  // 是否可以获取友链页面路由
  getFriendLinkRoute: boolean
  // 是否包含本站友链
  containsOurLink: boolean
}

let records = ref<Record[]>([])

// 获取友链监测记录
const fetchMonitorResult = async () => {
  axiosInstance
    .get('/apis/result.linkshealthmonitor.tch.cool/v1alpha1/latestResult')
    .then((res) => {
      const data = res.data.resultSpec
      console.log('结果...')
      console.log(data)
      // 插件配置
      config.value = {
        customizedCronEnable: data.customizedCronEnable ? '是' : '否',
        customizedCron: data.customizedCronEnable ? data.customizedCron : '-',
        customizedCronAvailable: data.customizedCronEnable
          ? data.customizedCronAvailable
            ? '是'
            : '否'
          : '-',
        externalUrl: data.externalUrl,
      }

      // 友链监测记录
      const mappedRecords = data.linkHealthMonitorRecordList.map((item: any, index: number) => ({
        no: index + 1,
        ...item,
      }));

      records.value = mappedRecords; // 修复赋值方式
    })
}

onMounted(() => {
  // 获取任务状态
  fetchTaskStatus()
})
</script>

<template>
  <VPageHeader title="Links Health Monitor">
    <template #icon>
      <IconDashboard class="mr-2 self-center" />
    </template>
  </VPageHeader>
  <div class=":uno: m-0 md:m-4">
    <VCard>
      <div class="friend-link-monitor">
        <!-- 主要内容区域 -->
        <div class="content">
          <!-- 状态卡片区域 -->
          <div class="status-cards">
            <!-- 任务状态 -->
            <div class="card task-status">
              <div class="card-header">
                <div class="card-title">
                  <svg
                    t="1759152352019"
                    class="icon"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="22426"
                    width="20"
                    height="20"
                  >
                    <path
                      d="M1009.371429 1024h-424.228572v-431.542857h424.228572v431.542857z m-351.085715-73.142857h277.942857v-285.257143h-277.942857v285.257143z m-234.057143 73.142857h-424.228571v-431.542857h424.228571v431.542857z m-351.085714-73.142857h277.942857v-285.257143h-277.942857v285.257143z m724.114286-490.057143c-58.514286 0-117.028571-21.942857-160.914286-65.828571-43.885714-43.885714-65.828571-95.085714-65.828571-160.914286s21.942857-117.028571 65.828571-160.914286c43.885714-43.885714 95.085714-65.828571 160.914286-65.828571s117.028571 21.942857 160.914286 65.828571c43.885714 43.885714 65.828571 95.085714 65.828571 160.914286s-21.942857 117.028571-65.828571 160.914286c-43.885714 43.885714-95.085714 65.828571-160.914286 65.828571z m0-387.657143c-43.885714 0-73.142857 14.628571-109.714286 43.885714-29.257143 29.257143-43.885714 65.828571-43.885714 109.714286s14.628571 80.457143 43.885714 109.714286c29.257143 29.257143 65.828571 43.885714 109.714286 43.885714s80.457143-14.628571 109.714286-43.885714c29.257143-29.257143 43.885714-65.828571 43.885714-109.714286s-14.628571-80.457143-43.885714-109.714286c-29.257143-29.257143-65.828571-43.885714-109.714286-43.885714z m-373.028572 380.342857h-424.228571v-431.542857h424.228571v431.542857z m-351.085714-73.142857h277.942857v-285.257143h-277.942857v285.257143z"
                      fill="#4285F6"
                      p-id="22427"
                    ></path>
                  </svg>
                  任务状态
                </div>
                <div class="status-badge running">
                  <span class="status-dot"></span>
                  {{ getTaskStatusLabel(taskStatus) }}
                </div>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">执行任务的Cron表达式</span>
                  <span class="value">{{ status.cronExpression }}</span>
                </div>
                <div class="info-item">
                  <span class="label">任务的计划执行时间</span>
                  <span class="value">{{ status.lastScheduledExecution }}</span>
                </div>
                <div class="info-item">
                  <span class="label">任务的执行开始时间</span>
                  <span class="value">{{ status.lastActualExecution }}</span>
                </div>
                <div class="info-item">
                  <span class="label">任务的执行完成时间</span>
                  <span class="value">{{ status.lastCompletionExecution }}</span>
                </div>
                <div class="info-item">
                  <span class="label">任务的实际执行时长</span>
                  <span class="value">{{ status.lastCompletionTime }}</span>
                </div>
                <div class="info-item">
                  <span class="label">下次任务的计划执行时间</span>
                  <span class="value">{{ status.nextScheduledExecution }}</span>
                </div>
                <div class="info-item">
                  <span class="label">距离下次任务执行的剩余时间</span>
                  <span class="value">{{ status.remainingTime }}</span>
                </div>
              </div>
            </div>

            <!-- 插件配置 -->
            <div class="card plugin-config">
              <div class="card-header">
                <div class="card-title">
                  <svg
                    t="1759153015378"
                    class="icon"
                    viewBox="0 0 1024 1024"
                    version="1.1"
                    xmlns="http://www.w3.org/2000/svg"
                    p-id="65398"
                    width="20"
                    height="20"
                  >
                    <path
                      d="M288 320c88.2 0 160-71.8 160-160s-71.8-160-160-160c-77.3 0-141.9 55.1-156.8 128H0v64h131.2c14.9 72.9 79.5 128 156.8 128z m0-256c52.9 0 96 43.1 96 96s-43.1 96-96 96-96-43.1-96-96 43.1-96 96-96zM288 704c-77.3 0-141.9 55.1-156.8 128H0v64h131.2c14.9 72.9 79.5 128 156.8 128 88.2 0 160-71.8 160-160s-71.8-160-160-160z m0 256c-52.9 0-96-43.1-96-96s43.1-96 96-96 96 43.1 96 96-43.1 96-96 96zM736.4 373.2c-88.2 0-160 71.8-160 160s71.8 160 160 160c73.4 0 135.4-49.7 154.2-117.2H1024v-64H895c-10.4-78.2-77.6-138.8-158.6-138.8z m0 256c-52.9 0-96-43.1-96-96s43.1-96 96-96c50.1 0 91.3 38.5 95.6 87.4v17.1c-4.3 49-45.6 87.5-95.6 87.5zM512 128H1024v64h-512zM0 512h512v64h-512zM512 832H1024v64h-512z"
                      fill="#4285F6"
                      p-id="65399"
                    ></path>
                  </svg>
                  插件配置
                </div>
              </div>
              <div class="card-content">
                <div class="info-item">
                  <span class="label">是否启用自定义Cron表达式</span>
                  <span class="value">{{ config.customizedCronEnable }}</span>
                </div>
                <div class="info-item">
                  <span class="label">自定义Cron</span>
                  <span class="value">{{ config.customizedCron }}</span>
                </div>
                <div class="info-item">
                  <span class="label">自定义Cron是否可用</span>
                  <span class="value">{{ config.customizedCronAvailable }}</span>
                </div>
                <div class="info-item">
                  <span class="label">本站外部地址</span>
                  <span class="value">{{ config.externalUrl }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 友链监测记录 -->
          <div class="card monitor-record">
            <div class="card-header">
              <div class="card-title">
                <svg
                  t="1759153356842"
                  class="icon"
                  viewBox="0 0 1024 1024"
                  version="1.1"
                  xmlns="http://www.w3.org/2000/svg"
                  p-id="105835"
                  width="20"
                  height="20"
                >
                  <path
                    d="M512 1000.727273C242.082909 1000.727273 23.272727 781.917091 23.272727 512S242.082909 23.272727 512 23.272727 1000.727273 242.082909 1000.727273 512 781.917091 1000.727273 512 1000.727273z m0-46.545455a442.181818 442.181818 0 1 0 0-884.363636 442.181818 442.181818 0 0 0 0 884.363636z m76.194909-624.733091c3.258182-4.002909 8.378182-4.794182 12.334546-1.861818 3.956364 2.978909 5.585455 8.843636 3.909818 14.103273l-76.520728 249.064727c-7.447273 26.018909-27.368727 43.938909-50.26909 45.288727-22.853818 1.349818-44.125091-14.149818-53.66691-39.098181-9.541818-25.041455-5.399273-54.458182 10.426182-74.286546l153.786182-193.210182z m-379.066182 186.321455l-45.893818-7.540364c35.607273-215.598545 153.460364-326.749091 348.765091-326.749091s313.157818 111.150545 348.765091 326.749091l-45.893818 7.540364C782.801455 321.768727 683.426909 228.072727 512 228.072727S241.198545 321.768727 209.128727 515.723636z"
                    fill="#4285F6"
                    p-id="105836"
                  ></path>
                </svg>
                友链监测记录
                <p class="tip-text">
                  任务状态为「任务成功」时才显示友链监测记录，因此若自定义Cron的间隔周期较短时，定时任务会频繁执行。
                </p>
              </div>
            </div>

            <!-- 无数据 -->
            <div
              v-if="!isShowResult(taskStatus)"
              class="empty-state"
              role="status"
              aria-label="暂无友链监测记录"
            >
              <img src="../assets/nodata.png" alt="暂无友链监测记录" loading="lazy" />
              <p>暂无友链监测记录</p>
            </div>

            <!-- 有数据 -->
            <div v-else class="table-container">
              <table class="record-table">
                <thead>
                  <tr>
                    <th>No</th>
                    <th>网站地址</th>
                    <th>网站名称</th>
                    <th>网站Logo</th>
                    <th>友链分组</th>
                    <th>网站是否可访问</th>
                    <th>Logo是否可访问</th>
                    <th>网站名称是否未变更</th>
                    <th>网站最新名称</th>
                    <th>是否可以获取友链页面路由</th>
                    <th>是否添加本站友链</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(record, index) in records" :key="index">
                    <!-- No -->
                    <td class="no-column">{{ record.no }}</td>
                    <!-- 网站地址 -->
                    <td class="url-column">
                      <a :href="record.linkUrl" target="_blank" class="url-link">{{ record.linkUrl }}</a>
                    </td>
                    <!-- 网站名称 -->
                    <td class="name-column">{{ record.linkDisplayName }}</td>
                    <!-- 网站Logo -->
                    <td class="center-cell">
                      <img class="link-logo" :src="record.linkLogo">
                    </td>
                    <!-- 友链分组 -->
                    <td class="group-column">{{ record.linkGroupDisplayName ?? '-' }}</td>
                    <!-- 网站是否可访问-->
                    <td class="center-cell">
                      <BooleanIcon :value="record.websiteAccessible" />
                    </td>
                    <!-- Logo是否可访问-->
                    <td class="center-cell">
                      <BooleanIcon :value="record.logoAccessible" />
                    </td>
                    <!-- 网站名称是否变更 -->
                    <td class="center-cell">
                      <BooleanIcon :value="record.displayNameChanged" />
                    </td>
                    <!-- 网站最新名称 -->
                    <td class="name-column">{{ record.latestDisplayName }}</td>
                    <!-- 是否可以获取友链页面路由 -->
                    <td class="center-cell">
                      <BooleanIcon :value="record.getFriendLinkRoute" />
                    </td>
                    <!-- 是否添加本站友链 -->
                    <td class="center-cell">
                      <BooleanIcon :value="record.containsOurLink" />
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </VCard>
  </div>
</template>

<style lang="scss" scoped>
// 变量定义
$primary-color: #2888bd;
$background-color: #f8f9fa;
$card-background: #ffffff;
$text-color: #2b2d42;
$text-light: #8d99ae;
$border-color: #e9ecef;
$border-radius: 8px;
$box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
$box-shadow-hover: 0 10px 15px rgba(0, 0, 0, 0.1);

.friend-link-monitor {
  // max-width: 1200px;
  margin: 0 auto;

  .content {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }
}

// 卡片样式
.card {
  background: $card-background;
  border-radius: $border-radius;
  box-shadow: $box-shadow;
  transition:
    transform 0.2s,
    box-shadow 0.2s;

  &:hover {
    box-shadow: $box-shadow-hover;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 20px;
    border-bottom: 1px solid $border-color;

    .card-title {
      display: flex;
      align-items: center;
      gap: 12px;
      font-weight: 500;
    }

    .status-badge {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 6px 14px;
      border-radius: 16px;
      font-size: 0.8rem;
      font-weight: 500;
      letter-spacing: 0.5px;
      text-transform: uppercase;
      transition: all 0.2s ease;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);

      &.running {
        background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
        color: #0369a1;
        border: 1px solid rgba(14, 165, 233, 0.2);
      }
    }

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background-color: currentColor;
      animation: pulse 2s infinite;
    }
  }

  .card-content {
    padding: 16px;
  }
}

.tip-text {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
  line-height: 1.4;
}

// 状态卡片布局
.status-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;

  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid rgba($border-color, 0.5);

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: $text-light;
      font-size: 0.875rem;
    }

    .value {
      font-size: 0.875rem;
    }
  }
}

// 表格样式
.monitor-record {

  .table-container {
    overflow-x: auto;
    max-height: 650px;
    overflow-y: auto;
  }

  .record-table {
    width: 100%;
    border-collapse: collapse;

    thead {
      position: sticky;
      top: 0;
      z-index: 10;
      background-color: $card-background; // 添加不透明背景色

      tr {
        th {
          padding: 16px 12px;
          text-align: center;
          vertical-align: middle;
          color: $text-light;
          border-bottom: 2px solid $border-color;
          white-space: nowrap;
          font-weight: normal;
          background-color: $card-background; // 添加不透明背景色
        }
      }
    }

    tbody {
      tr {
        transition: background-color 0.2s;

        &:hover {
          background-color: rgba($primary-color, 0.03);
        }

        &:not(:last-child) {
          border-bottom: 1px solid $border-color;
        }

        td {
          padding: 16px 12px;
          text-align: center;
          vertical-align: middle;
        }
      }
    }

    .no-column {
      width: 60px;
      font-weight: 500;
      color: $text-light;
    }

    .url-column {
      max-width: 200px;

      .url-link {
        color: $primary-color;
        text-decoration: none;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        display: block;

        &:hover {
          text-decoration: underline;
        }
      }
    }

    .name-column {
      font-weight: 400;
      font-weight: normal;
      max-width: 200px;
    }

    //.center-cell {
      //display: flex;
      //justify-content: center; /* 水平居中 */
      //align-items: center;     /* 垂直居中 */
      //height: 100%;            /* 确保占满单元格高度 */
    //}

    .link-logo {
      height: 30px;
      width: 30px;
    }

    .group-column {
      align-content: center;
    }

    .boolean-column {
      width: 100px;
      min-width: 100px;
    }
  }

  .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 16px; // 替代 margin/padding 控制间距
    padding: 40px;
    text-align: center;
    color: $text-light;

    // 图片自适应，防止溢出
    img {
      max-width: 100%;
      height: auto;
      // 可选：设置一个合理的最大高度或宽度
      max-height: 200px;
    }

    p {
      margin: 0;
      font-size: 1rem;
      line-height: 1.5;
    }

    // 响应式：小屏设备缩小字体和内边距
    @media (max-width: 600px) {
      padding: 24px;
      p {
        font-size: 1rem;
      }
      img {
        max-height: 120px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .friend-link-monitor {
    padding: 10px;

    .header h1 {
      font-size: 2rem;
    }

    .status-cards {
      grid-template-columns: 1fr;
    }

    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
    }
  }
}
</style>
