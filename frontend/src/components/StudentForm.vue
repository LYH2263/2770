<template>
  <el-form
    ref="formRef"
    :model="form"
    :rules="rules"
    label-position="top"
    class="student-form p-2"
  >
    <div class="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-2">
      <el-form-item label="学号" prop="studentNo">
        <el-input v-model="form.studentNo" placeholder="请输入学号" :prefix-icon="Postcard" class="custom-input" />
      </el-form-item>
      
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" :prefix-icon="User" class="custom-input" />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender" class="flex space-x-4">
          <el-radio-button label="男">男生</el-radio-button>
          <el-radio-button label="女">女生</el-radio-button>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="年龄" prop="age">
        <el-input-number v-model="form.age" :min="1" :max="100" class="!w-full custom-number-input" />
      </el-form-item>

      <el-form-item label="专业" prop="major">
        <el-input v-model="form.major" placeholder="请输入专业" :prefix-icon="Collection" class="custom-input" />
      </el-form-item>

      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入11位手机号" :prefix-icon="Iphone" class="custom-input" />
      </el-form-item>

      <el-form-item label="电子邮箱" prop="email" class="md:col-span-2">
        <el-input v-model="form.email" placeholder="请输入正确格式的邮箱地址" :prefix-icon="Message" class="custom-input" />
      </el-form-item>
    </div>

    <div class="mt-10 flex justify-end space-x-4 border-t border-gray-100 pt-8">
      <el-button @click="$emit('cancel')" class="!rounded-xl px-8 !h-12 !border-gray-200 hover:!bg-gray-50 !text-gray-500 font-bold">
        取消返回
      </el-button>
      <el-button
        type="primary"
        :loading="submitting"
        class="!rounded-xl px-10 !h-12 !bg-blue-600 !border-blue-600 shadow-lg shadow-blue-100 font-bold transform active:scale-95 transition-all"
        @click="handleSubmit"
      >
        推送档案
      </el-button>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { addStudent, updateStudent } from '../api/student'
import { ElMessage } from 'element-plus'
import { User, Iphone, Message, Collection, Postcard } from '@element-plus/icons-vue'

const props = defineProps({
  initialData: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['success', 'cancel'])
const formRef = ref(null)
const submitting = ref(false)

const form = reactive({
  id: null,
  studentNo: '',
  name: '',
  gender: '男',
  age: 18,
  major: '',
  phone: '',
  email: ''
})

const rules = {
  studentNo: [{ required: true, message: '学号是档案的核心标识', trigger: 'blur' }],
  name: [{ required: true, message: '请输入学生姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入有效年龄', trigger: 'blur' }],
  major: [{ required: true, message: '专业信息不可缺失', trigger: 'blur' }],
  phone: [
    { required: true, message: '联系电话必填', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入11位中国大陆手机号', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '邮箱地址必填', trigger: 'blur' },
    { type: 'email', message: '邮箱格式校验未通过', trigger: 'blur' }
  ]
}

onMounted(() => {
  if (props.initialData) {
    Object.assign(form, props.initialData)
  }
})

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        const apiFunc = form.id ? updateStudent : addStudent
        const res = await apiFunc(form)
        if (res.code === 200) {
          ElMessage.success({
            message: form.id ? '信息核改成功' : '档案建立成功',
            plain: true
          })
          emit('success')
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error(error)
      } finally {
        submitting.value = false
      }
    }
  })
}
</script>

<style scoped>
.custom-input :deep(.el-input__wrapper) {
  background-color: #f8fafc;
  border-radius: 12px;
  box-shadow: none !important;
  border: 1px solid #e2e8f0;
  padding: 8px 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.custom-input :deep(.el-input__wrapper.is-focus) {
  background-color: white;
  border-color: #3b82f6;
  box-shadow: 0 0 0 4px rgba(59, 130, 246, 0.08) !important;
}

.custom-number-input :deep(.el-input__wrapper) {
  border-radius: 12px;
  background-color: #f8fafc;
}

:deep(.el-form-item__label) {
  font-weight: 700;
  color: #475569;
  font-size: 13px;
  margin-bottom: 8px !important;
  padding: 0 4px !important;
}

:deep(.el-radio-button__inner) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  box-shadow: none !important;
  margin-right: 12px;
  height: 40px;
  line-height: 40px;
  padding: 0 24px;
  font-weight: 600;
  color: #64748b;
  background: white;
}

:deep(.el-radio-button:first-child .el-radio-button__inner) {
  border-left: 1px solid #e2e8f0 !important;
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background-color: #3b82f6 !important;
  border-color: #3b82f6 !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25) !important;
}
</style>
