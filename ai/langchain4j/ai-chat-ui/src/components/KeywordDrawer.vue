<template>
  <el-drawer
      :visible.sync="internalVisible"
      :direction="direction"
      size="40%"
      :before-close="handleClose"
      :wrapperClosable="true"
      custom-class="keyword-drawer"
  >
    <div class="drawer-header">
      <h3>相关资源链接</h3>
      <el-button
          type="text"
          icon="el-icon-close"
          @click="closeDrawer"
          class="close-btn"
      ></el-button>
    </div>

    <el-divider></el-divider>

    <div class="drawer-body">
      <el-table
          :data="urls"
          style="width: 100%"
          :show-header="false"
          @row-click="handleRowClick"
          empty-text="暂无相关链接"
      >
        <el-table-column width="40">
          <template #default>
            <i class="el-icon-link"></i>
          </template>
        </el-table-column>

        <el-table-column>
          <template #default="{ row }">
            <div class="link-item">
              <div class="link-title">{{ row.title }}</div>
              <div class="link-date" v-if="row.date">{{ formatDate(row.date) }}</div>
              <div class="link-desc" v-if="row.description">{{ row.description }}</div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-drawer>
</template>

<script>
export default {
  name: 'KeywordDrawer',
  props: {
    urls: {
      type: Array,
      default: () => []
    },
    visible: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      direction: 'rtl',
      internalVisible: this.visible
    }
  },
  watch: {
    visible(newVal) {
      this.internalVisible = newVal
    }
  },
  methods: {
    handleClose(done) {
      this.$emit('update:visible', false)
      done()
    },
    closeDrawer() {
      this.internalVisible = false
      this.$emit('update:visible', false)
    },
    handleRowClick(row) {
      if (row.url) {
        window.open(row.url, '_blank')
      }
    },
    formatDate(timestamp) {
      if (!timestamp) return '';
      const date = new Date(timestamp);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:${date.getSeconds().toString().padStart(2, '0')}`;
    }
  }
}
</script>

<style scoped>
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 20px 0 20px;
}

.drawer-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.close-btn {
  padding: 0;
  font-size: 18px;
}

.drawer-body {
  padding: 0 20px 20px 20px;
}

.link-item {
  padding: 8px 0;
}

.link-title {
  font-weight: 500;
  color: #409EFF;
  margin-bottom: 4px;
}

.link-date {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.link-desc {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
}

/* 表格行悬停效果 */
:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__row:hover) {
  background-color: #f5f7fa;
}

:deep(.el-table__row:hover .link-title) {
  text-decoration: underline;
}

/* 抽屉自定义样式 */
:deep(.keyword-drawer) {
  box-shadow: -2px 0 12px rgba(0, 0, 0, 0.1);
}

:deep(.keyword-drawer .el-drawer__header) {
  display: none;
}

:deep(.keyword-drawer .el-drawer__body) {
  padding: 0;
  display: flex;
  flex-direction: column;
  height: 100%;
}
</style>