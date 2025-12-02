<template>
  <div class="registration-page">
    <h2>门诊挂号</h2>
    <div class="dept-select">
      <label>选择科室：</label>
      <select class="select">
        <option value="">请选择科室</option>
        <option value="内科">内科</option>
        <option value="外科">外科</option>
        <option value="儿科">儿科</option>
        <option value="妇产科">妇产科</option>
      </select>
    </div>
    <!-- 挂号卡片列表 -->
    <div class="card-list">
      <RegistrationCard 
        v-for="(item, index) in doctorList" 
        :key="index"
        :doctorName="item.doctorName"
        :department="item.department"
        :title="item.title"
        :remainNum="item.remainNum"
      />
    </div>
  </div>
</template>

<script>
// 导入挂号卡片组件
import RegistrationCard from '@/components/RegistrationCard'

export default {
  components: {
    RegistrationCard
  },
  data() {
    return {
      // 模拟医生数据
      doctorList: [
        { doctorName: '张三', department: '内科', title: '主任医师', remainNum: 5 },
        { doctorName: '李四', department: '内科', title: '副主任医师', remainNum: 12 },
        { doctorName: '王五', department: '外科', title: '主治医师', remainNum: 8 }
      ]
    }
  }
}
</script>

<style scoped>
.registration-page h2 {
  color: #2f5496;
  margin-bottom: 20px;
}
.dept-select {
  margin-bottom: 20px;
}
.select {
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-left: 10px;
}
.card-list {
  margin-top: 20px;
}
</style>