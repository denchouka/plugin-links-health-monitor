import { definePlugin } from '@halo-dev/console-shared'
import LinksHealthMonitor from './views/LinksHealthMonitor.vue'
import { VLoading, IconDashboard } from '@halo-dev/components'
import { markRaw, defineAsyncComponent } from 'vue'

export default definePlugin({
  components: {},
  routes: [
    {
      parentName: 'ToolsRoot',
      route: {
        path: '/linksHealth',
        name: 'LinksHealthMonitor',
        component: LinksHealthMonitor,
        meta: {
          title: 'Links Health Monitor',
          searchable: true,
          menu: {
            name: 'Links Health Monitor',
            group: "tool",
            icon: markRaw(IconDashboard),
            priority: 0,
            mobile: true
          },
        },
      },
    },
  ],
  extensionPoints: {"plugin:self:tabs:create": () => {
      return [
        {
          id: "links-health-monitor-settings",
          label: "设置",
          component: defineAsyncComponent({
            loader: () => import("./views/LinksHealthSettings.vue"),
            loadingComponent: VLoading,
          }),
          permissions: ["*"],
        },
      ];
    },
  },
})
