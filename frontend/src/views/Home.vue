<template>
  <div style="min-height: 100vh; background: #f5f7fa;">
    <el-header style="background: #fff; box-shadow: 0 2px 8px rgba(0,0,0,0.1); display: flex; align-items: center; justify-content: space-between; padding: 0 24px;">
      <div style="display: flex; align-items: center; gap: 12px;">
        <el-icon :size="28" color="#409eff"><Calendar /></el-icon>
        <h2 style="margin: 0; font-size: 20px;">会议室预订系统</h2>
      </div>
      <div style="display: flex; align-items: center; gap: 16px;">
        <span>欢迎，{{ userInfo?.realName }} ({{ getRoleLabel(userInfo?.roleType) }})</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </div>
    </el-header>

    <el-main style="padding: 24px;">
      <el-card style="margin-bottom: 20px;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <h3 style="margin: 0;">今日会议室占用情况</h3>
          <el-button type="primary" @click="openBookingDialog">
            <el-icon><Plus /></el-icon>
            新建预订
          </el-button>
        </div>
        <TimelineView :rooms="rooms" :bookings="todayBookings" @booking-click="handleBookingClick" />
      </el-card>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-card>
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>我的预订</span>
                <el-button type="primary" link size="small" @click="loadMyBookings">刷新</el-button>
              </div>
            </template>
            <el-table :data="myBookings" style="width: 100%" size="small">
              <el-table-column prop="title" label="会议主题" min-width="120" />
              <el-table-column prop="roomId" label="会议室" width="100">
                <template #default="{ row }">
                  {{ getRoomName(row.roomId) }}
                </template>
              </el-table-column>
              <el-table-column label="时间" width="200">
                <template #default="{ row }">
                  {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime, true) }}
                </template>
              </el-table-column>
              <el-table-column prop="bookingStatus" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.bookingStatus)">
                    {{ getStatusLabel(row.bookingStatus) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ row }">
                  <el-button
                    v-if="row.bookingStatus === 'PENDING' || row.bookingStatus === 'APPROVED'"
                    type="danger"
                    link
                    size="small"
                    @click="handleCancelBooking(row.id)"
                  >
                    取消
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="12">
          <el-card v-if="canApprove">
            <template #header>
              <div style="display: flex; justify-content: space-between; align-items: center;">
                <span>待审批预订</span>
                <el-button type="primary" link size="small" @click="loadPendingBookings">刷新</el-button>
              </div>
            </template>
            <el-table :data="pendingBookings" style="width: 100%" size="small">
              <el-table-column prop="title" label="会议主题" min-width="120" />
              <el-table-column prop="roomId" label="会议室" width="100">
                <template #default="{ row }">
                  {{ getRoomName(row.roomId) }}
                </template>
              </el-table-column>
              <el-table-column label="时间" width="200">
                <template #default="{ row }">
                  {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime, true) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="{ row }">
                  <el-button type="success" link size="small" @click="handleApprove(row.id, true)">
                    批准
                  </el-button>
                  <el-button type="danger" link size="small" @click="handleApprove(row.id, false)">
                    拒绝
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <el-dialog v-model="bookingDialogVisible" title="预订会议室" width="500px">
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="会议室" prop="roomId">
          <el-select v-model="bookingForm.roomId" style="width: 100%;">
            <el-option
              v-for="room in rooms"
              :key="room.id"
              :label="room.roomName + ' (' + room.capacity + '人)'"
              :value="room.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="会议主题" prop="title">
          <el-input v-model="bookingForm.title" placeholder="请输入会议主题" />
        </el-form-item>
        <el-form-item label="用途说明" prop="purpose">
          <el-input v-model="bookingForm.purpose" type="textarea" :rows="3" placeholder="请输入用途说明" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="bookingForm.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%;"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="bookingForm.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%;"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-form-item>
        <el-form-item label="参会人数">
          <el-input-number v-model="bookingForm.attendeeCount" :min="1" :max="100" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bookingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBooking" :loading="submitting">提交预订</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="bookingDetailVisible" title="预订详情" width="400px">
      <el-descriptions :column="1" border size="small" v-if="selectedBooking">
        <el-descriptions-item label="会议主题">{{ selectedBooking.title }}</el-descriptions-item>
        <el-descriptions-item label="会议室">{{ selectedBooking.roomName }}</el-descriptions-item>
        <el-descriptions-item label="预订人">{{ selectedBooking.userName }}</el-descriptions-item>
        <el-descriptions-item label="用途">{{ selectedBooking.purpose }}</el-descriptions-item>
        <el-descriptions-item label="时间">
          {{ formatTime(selectedBooking.startTime) }} - {{ formatTime(selectedBooking.endTime, true) }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(selectedBooking.bookingStatus)">
            {{ getStatusLabel(selectedBooking.bookingStatus) }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Calendar, Plus } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  logout,
  getRoomList,
  getTodayBookings,
  createBooking,
  cancelBooking,
  getMyBookings,
  getPendingBookings,
  approveBooking
} from '@/api'
import { BookingStatus, RoleType } from '@/types'
import TimelineView from '@/components/TimelineView.vue'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const rooms = ref([])
const todayBookings = ref([])
const myBookings = ref([])
const pendingBookings = ref([])
const bookingDialogVisible = ref(false)
const bookingDetailVisible = ref(false)
const selectedBooking = ref(null)
const submitting = ref(false)
const bookingFormRef = ref(null)

const canApprove = computed(() => {
  return userInfo.value?.roleType === 'ADMIN' || userInfo.value?.roleType === 'MANAGER'
})

const bookingForm = ref({
  roomId: null,
  title: '',
  purpose: '',
  startTime: '',
  endTime: '',
  attendeeCount: 1
})

const bookingRules = {
  roomId: [{ required: true, message: '请选择会议室', trigger: 'change' }],
  title: [{ required: true, message: '请输入会议主题', trigger: 'blur' }],
  purpose: [{ required: true, message: '请输入用途说明', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

const loadData = async () => {
  try {
    rooms.value = await getRoomList()
    todayBookings.value = await getTodayBookings()
    await loadMyBookings()
    if (canApprove.value) {
      await loadPendingBookings()
    }
  } catch (error) {
    console.error(error)
  }
}

const loadMyBookings = async () => {
  myBookings.value = await getMyBookings()
}

const loadPendingBookings = async () => {
  pendingBookings.value = await getPendingBookings()
}

const getRoomName = (roomId) => {
  const room = rooms.value.find(r => r.id === roomId)
  return room ? room.roomName : '未知'
}

const formatTime = (time, onlyTime = false) => {
  if (!time) return ''
  const format = onlyTime ? 'HH:mm' : 'MM-DD HH:mm'
  return dayjs(time).format(format)
}

const getStatusLabel = (status) => {
  return BookingStatus[status]?.label || status
}

const getStatusType = (status) => {
  return BookingStatus[status]?.color || 'info'
}

const getRoleLabel = (role) => {
  return RoleType[role]?.label || role
}

const openBookingDialog = () => {
  bookingForm.value = {
    roomId: rooms.value[0]?.id || null,
    title: '',
    purpose: '',
    startTime: '',
    endTime: '',
    attendeeCount: 1
  }
  bookingDialogVisible.value = true
}

const submitBooking = async () => {
  try {
    await bookingFormRef.value.validate()
    submitting.value = true
    await createBooking(bookingForm.value)
    ElMessage.success('预订提交成功，等待审批')
    bookingDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  } finally {
    submitting.value = false
  }
}

const handleCancelBooking = async (id) => {
  try {
    await ElMessageBox.confirm('确定要取消此预订吗？', '提示', {
      type: 'warning'
    })
    await cancelBooking(id)
    ElMessage.success('取消成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleApprove = async (bookingId, isApprove) => {
  try {
    const action = isApprove ? '批准' : '拒绝'
    await ElMessageBox.confirm(`确定要${action}此预订吗？`, '提示', {
      type: 'warning'
    })
    await approveBooking({
      bookingId,
      approvalStatus: isApprove ? 'APPROVED' : 'REJECTED'
    })
    ElMessage.success(`${action}成功`)
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

const handleBookingClick = (booking) => {
  selectedBooking.value = booking
  bookingDetailVisible.value = true
}

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      type: 'warning'
    })
    await logout()
    localStorage.removeItem('satoken')
    localStorage.removeItem('userInfo')
    router.push('/login')
  } catch (error) {
    if (error !== 'cancel') {
      console.error(error)
    }
  }
}

onMounted(() => {
  loadData()
})
</script>
