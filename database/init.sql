-- =====================================================
-- 企业会议室预订系统 - 数据库初始化脚本
-- 数据库类型: PostgreSQL
-- 版本: 1.0
-- =====================================================

-- 如果已存在则删除表（按依赖顺序删除）
DROP TABLE IF EXISTS t_approval CASCADE;
DROP TABLE IF EXISTS t_booking CASCADE;
DROP TABLE IF EXISTS t_meeting_room CASCADE;
DROP TABLE IF EXISTS t_user CASCADE;
DROP TABLE IF EXISTS t_department CASCADE;

-- =====================================================
-- 1. 部门表
-- =====================================================
CREATE TABLE t_department (
    id              BIGSERIAL PRIMARY KEY,
    dept_name       VARCHAR(100) NOT NULL UNIQUE,
    dept_code       VARCHAR(50)  NOT NULL UNIQUE,
    parent_id       BIGINT,
    sort_order      INT DEFAULT 0,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_department IS '部门表';
COMMENT ON COLUMN t_department.id IS '主键ID';
COMMENT ON COLUMN t_department.dept_name IS '部门名称';
COMMENT ON COLUMN t_department.dept_code IS '部门编码';
COMMENT ON COLUMN t_department.parent_id IS '父部门ID';
COMMENT ON COLUMN t_department.sort_order IS '排序号';
COMMENT ON COLUMN t_department.create_time IS '创建时间';
COMMENT ON COLUMN t_department.update_time IS '更新时间';

-- =====================================================
-- 2. 用户表（员工表）
-- =====================================================
CREATE TABLE t_user (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(100) NOT NULL,
    real_name       VARCHAR(50)  NOT NULL,
    email           VARCHAR(100),
    phone           VARCHAR(20),
    dept_id         BIGINT REFERENCES t_department(id),
    role_type       VARCHAR(20)  NOT NULL DEFAULT 'EMPLOYEE',
    status          INT DEFAULT 1,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_user IS '用户表（员工表）';
COMMENT ON COLUMN t_user.id IS '主键ID';
COMMENT ON COLUMN t_user.username IS '登录账号';
COMMENT ON COLUMN t_user.password IS '密码（加密）';
COMMENT ON COLUMN t_user.real_name IS '真实姓名';
COMMENT ON COLUMN t_user.email IS '邮箱';
COMMENT ON COLUMN t_user.phone IS '手机号';
COMMENT ON COLUMN t_user.dept_id IS '所属部门ID';
COMMENT ON COLUMN t_user.role_type IS '角色类型：ADMIN-管理员 MANAGER-部门经理 EMPLOYEE-普通员工';
COMMENT ON COLUMN t_user.status IS '状态：1-启用 0-禁用';
COMMENT ON COLUMN t_user.create_time IS '创建时间';
COMMENT ON COLUMN t_user.update_time IS '更新时间';

-- =====================================================
-- 3. 会议室表
-- =====================================================
CREATE TABLE t_meeting_room (
    id              BIGSERIAL PRIMARY KEY,
    room_name       VARCHAR(100) NOT NULL UNIQUE,
    room_code       VARCHAR(50)  NOT NULL UNIQUE,
    capacity        INT NOT NULL,
    location        VARCHAR(200) NOT NULL,
    facilities      VARCHAR(500),
    room_status     INT DEFAULT 1,
    description     TEXT,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_meeting_room IS '会议室表';
COMMENT ON COLUMN t_meeting_room.id IS '主键ID';
COMMENT ON COLUMN t_meeting_room.room_name IS '会议室名称';
COMMENT ON COLUMN t_meeting_room.room_code IS '会议室编号';
COMMENT ON COLUMN t_meeting_room.capacity IS '容纳人数';
COMMENT ON COLUMN t_meeting_room.location IS '位置';
COMMENT ON COLUMN t_meeting_room.facilities IS '设备设施（逗号分隔：投影仪,白板,视频会议等）';
COMMENT ON COLUMN t_meeting_room.room_status IS '状态：1-可用 0-不可用';
COMMENT ON COLUMN t_meeting_room.description IS '描述';
COMMENT ON COLUMN t_meeting_room.create_time IS '创建时间';
COMMENT ON COLUMN t_meeting_room.update_time IS '更新时间';

-- =====================================================
-- 4. 预订表
-- =====================================================
CREATE TABLE t_booking (
    id              BIGSERIAL PRIMARY KEY,
    room_id         BIGINT NOT NULL REFERENCES t_meeting_room(id),
    user_id         BIGINT NOT NULL REFERENCES t_user(id),
    title           VARCHAR(200) NOT NULL,
    purpose         TEXT NOT NULL,
    start_time      TIMESTAMP NOT NULL,
    end_time        TIMESTAMP NOT NULL,
    attendee_count  INT,
    booking_status  VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_booking IS '预订表';
COMMENT ON COLUMN t_booking.id IS '主键ID';
COMMENT ON COLUMN t_booking.room_id IS '会议室ID';
COMMENT ON COLUMN t_booking.user_id IS '预订人ID';
COMMENT ON COLUMN t_booking.title IS '会议主题';
COMMENT ON COLUMN t_booking.purpose IS '用途说明';
COMMENT ON COLUMN t_booking.start_time IS '开始时间';
COMMENT ON COLUMN t_booking.end_time IS '结束时间';
COMMENT ON COLUMN t_booking.attendee_count IS '参会人数';
COMMENT ON COLUMN t_booking.booking_status IS '预订状态：PENDING-待审批 APPROVED-已批准 REJECTED-已拒绝 CANCELLED-已取消';
COMMENT ON COLUMN t_booking.create_time IS '创建时间';
COMMENT ON COLUMN t_booking.update_time IS '更新时间';

-- 为时间范围查询创建索引
CREATE INDEX idx_booking_time ON t_booking(room_id, start_time, end_time);
CREATE INDEX idx_booking_status ON t_booking(booking_status);

-- =====================================================
-- 5. 审批表
-- =====================================================
CREATE TABLE t_approval (
    id              BIGSERIAL PRIMARY KEY,
    booking_id      BIGINT NOT NULL REFERENCES t_booking(id),
    approver_id     BIGINT NOT NULL REFERENCES t_user(id),
    approval_status VARCHAR(20) NOT NULL,
    approval_comment TEXT,
    approval_time   TIMESTAMP,
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE t_approval IS '审批表';
COMMENT ON COLUMN t_approval.id IS '主键ID';
COMMENT ON COLUMN t_approval.booking_id IS '预订ID';
COMMENT ON COLUMN t_approval.approver_id IS '审批人ID';
COMMENT ON COLUMN t_approval.approval_status IS '审批状态：APPROVED-批准 REJECTED-拒绝';
COMMENT ON COLUMN t_approval.approval_comment IS '审批意见';
COMMENT ON COLUMN t_approval.approval_time IS '审批时间';
COMMENT ON COLUMN t_approval.create_time IS '创建时间';
COMMENT ON COLUMN t_approval.update_time IS '更新时间';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入部门数据
INSERT INTO t_department (dept_name, dept_code, parent_id, sort_order) VALUES
('技术部', 'TECH', NULL, 1),
('产品部', 'PRODUCT', NULL, 2),
('市场部', 'MARKET', NULL, 3),
('人事部', 'HR', NULL, 4),
('财务部', 'FINANCE', NULL, 5),
('开发一组', 'DEV1', 1, 1),
('开发二组', 'DEV2', 1, 2),
('测试组', 'TEST', 1, 3);

-- 插入用户数据（密码都是 123456，使用 BCrypt 加密）
INSERT INTO t_user (username, password, real_name, email, phone, dept_id, role_type) VALUES
('admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '系统管理员', 'admin@company.com', '13800000000', NULL, 'ADMIN'),
('zhangsan', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '张三', 'zhangsan@company.com', '13800000001', 1, 'MANAGER'),
('lisi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '李四', 'lisi@company.com', '13800000002', 2, 'MANAGER'),
('wangwu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '王五', 'wangwu@company.com', '13800000003', 6, 'EMPLOYEE'),
('zhaoliu', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '赵六', 'zhaoliu@company.com', '13800000004', 7, 'EMPLOYEE'),
('qianqi', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '钱七', 'qianqi@company.com', '13800000005', 3, 'EMPLOYEE');

-- 插入会议室数据
INSERT INTO t_meeting_room (room_name, room_code, capacity, location, facilities, description) VALUES
('大会议室A', 'ROOM-A01', 50, '3楼东侧', '投影仪,白板,视频会议,音响系统', '大型会议室，适合部门级会议'),
('中会议室B', 'ROOM-B01', 20, '2楼北侧', '投影仪,白板,视频会议', '中型会议室，适合团队会议'),
('小会议室C', 'ROOM-C01', 8, '1楼南侧', '投影仪,白板', '小型会议室，适合小组讨论'),
('培训室', 'ROOM-T01', 30, '4楼', '投影仪,白板,音响系统,麦克风', '培训专用'),
('洽谈室', 'ROOM-M01', 6, '1楼大厅旁', '白板,电视', '客户洽谈用');

-- 插入一些测试预订数据
INSERT INTO t_booking (room_id, user_id, title, purpose, start_time, end_time, attendee_count, booking_status) VALUES
(1, 4, '技术周会', '讨论本周技术进展和下周计划', '2026-05-19 09:00:00', '2026-05-19 10:30:00', 15, 'APPROVED'),
(2, 5, '产品评审', '新产品需求评审', '2026-05-19 10:00:00', '2026-05-19 12:00:00', 10, 'APPROVED'),
(3, 6, '客户洽谈', '与客户讨论项目合作', '2026-05-19 14:00:00', '2026-05-19 16:00:00', 4, 'PENDING'),
(1, 6, '全员大会', '公司全体员工大会', '2026-05-19 15:00:00', '2026-05-19 17:00:00', 50, 'PENDING');

-- 插入审批数据
INSERT INTO t_approval (booking_id, approver_id, approval_status, approval_comment, approval_time) VALUES
(1, 2, 'APPROVED', '同意', '2026-05-18 10:00:00'),
(2, 3, 'APPROVED', '同意', '2026-05-18 11:30:00');
