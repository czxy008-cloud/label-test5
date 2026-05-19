export const BookingStatus = {
  PENDING: { value: 'PENDING', label: '待审批', color: 'warning' },
  APPROVED: { value: 'APPROVED', label: '已批准', color: 'success' },
  REJECTED: { value: 'REJECTED', label: '已拒绝', color: 'danger' },
  CANCELLED: { value: 'CANCELLED', label: '已取消', color: 'info' }
}

export const RoleType = {
  ADMIN: { value: 'ADMIN', label: '管理员' },
  MANAGER: { value: 'MANAGER', label: '部门经理' },
  EMPLOYEE: { value: 'EMPLOYEE', label: '普通员工' }
}
