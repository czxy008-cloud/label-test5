<template>
  <div class="timeline-container">
    <div class="timeline-time-header">
      <div class="timeline-time-cell" style="height: 49px;"></div>
      <div
        v-for="hour in hours"
        :key="hour"
        class="timeline-time-cell"
      >
        {{ String(hour).padStart(2, '0') }}:00
      </div>
    </div>

    <div class="timeline-rooms">
      <div class="timeline-room-header">
        <div
          v-for="room in rooms"
          :key="room.id"
          class="timeline-room-name"
        >
          <div>{{ room.roomName }}</div>
          <div style="font-size: 12px; color: #909399; font-weight: normal;">
            {{ room.location }} · {{ room.capacity }}人
          </div>
        </div>
      </div>

      <div class="timeline-body">
        <div class="timeline-row">
          <div
            v-for="room in rooms"
            :key="room.id"
            class="timeline-room-column"
          >
            <div v-for="hour in hours" :key="hour" class="timeline-cell"></div>

            <div
              v-for="booking in getRoomBookings(room.id)"
              :key="booking.id"
              :class="['booking-block', booking.bookingStatus.toLowerCase()]"
              :style="getBookingStyle(booking)"
              @click="$emit('booking-click', booking)"
            >
              <div class="booking-title">{{ booking.title }}</div>
              <div class="booking-time">
                {{ formatTime(booking.startTime) }} - {{ formatTime(booking.endTime, true) }}
              </div>
            </div>
          </div>
        </div>

        <div
          v-if="currentTimePosition !== null"
          class="current-time-line"
          :style="{ top: currentTimePosition + 'px' }"
        ></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import dayjs from 'dayjs'

const props = defineProps({
  rooms: {
    type: Array,
    default: () => []
  },
  bookings: {
    type: Array,
    default: () => []
  }
})

defineEmits(['booking-click'])

const hours = computed(() => {
  const arr = []
  for (let i = 8; i <= 20; i++) {
    arr.push(i)
  }
  return arr
})

const currentTimePosition = ref(null)
let timer = null

const getRoomBookings = (roomId) => {
  return props.bookings.filter(b => b.roomId === roomId)
}

const getBookingStyle = (booking) => {
  const cellHeight = 48
  const startHour = dayjs(booking.startTime).hour() + dayjs(booking.startTime).minute() / 60
  const endHour = dayjs(booking.endTime).hour() + dayjs(booking.endTime).minute() / 60

  const startOffset = (startHour - 8) * cellHeight
  const duration = (endHour - startHour) * cellHeight

  return {
    top: `${startOffset}px`,
    height: `${Math.max(duration - 4, 30)}px`
  }
}

const formatTime = (time, onlyTime = false) => {
  if (!time) return ''
  const format = onlyTime ? 'HH:mm' : 'HH:mm'
  return dayjs(time).format(format)
}

const updateCurrentTime = () => {
  const now = dayjs()
  const currentHour = now.hour() + now.minute() / 60
  if (currentHour >= 8 && currentHour <= 20) {
    currentTimePosition.value = (currentHour - 8) * 48
  } else {
    currentTimePosition.value = null
  }
}

onMounted(() => {
  updateCurrentTime()
  timer = setInterval(updateCurrentTime, 60000)
})

onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>
