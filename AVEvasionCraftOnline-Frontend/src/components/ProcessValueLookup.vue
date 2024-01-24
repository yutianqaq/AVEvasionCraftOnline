<template>
    <div class="container">
        <div>
            <el-text class="mx-1" size="large">数据来源：https://github.com/r00tSe7en/get_AV</el-text>
        </div>
        <el-input class="custom-input" v-model="processNames" :rows="5" type="textarea" placeholder="在这里输入 tasklist 结果" />
        <div class="button-container">
            <el-button type="primary" @click="getProcessValues">点击识别</el-button>
        </div>
        <!-- https://github.com/yutianqaq -->
        <el-text class="mx-1" size="large">总共 {{ Object.keys(processValues).length }} 个结果</el-text>
        <el-table v-if="Object.keys(processValues).length > 0" :data="processTableData" class="result-table">
            <el-table-column prop="name" label="进程名称"></el-table-column>
            <el-table-column prop="value" label="杀软名称"></el-table-column>
        </el-table>
    </div>
</template>
  
<script>
import axios from 'axios';
import { ElNotification } from 'element-plus';

export default {
    name: 'ProcessValueLookup',
    data() {
        return {
            processNames: '',
            processValues: {},
            processes: null,
        };
    },
    computed: {
        processTableData() {
            return Object.entries(this.processValues).map(([name, value]) => ({ name, value }));
        },
    },
    methods: {
        async getProcessValues() {
            try {
                const response = await axios.get('/av.json');
                this.processes = response.data;

                const lines = this.processNames.split('\n');
                this.processValues = {};

                lines.forEach(line => {
                    const [name] = line.trim().split(/\s+/); 

                    if (name) {
                        const matchingProcess = Object.keys(this.processes).find(processName =>
                            new RegExp(`^${name}$`, 'i').test(processName)
                        );

                        if (matchingProcess) {
                            this.processValues[matchingProcess] = this.processes[matchingProcess];
                        }
                    }
                });
                if (Object.keys(this.processValues).length > 0) {
                    this.showNotification('Success', '成功找到进程杀软信息', 'success');
                } else {
                    this.showNotification('Error', '没有找到或暂未收录', 'error');
                }

            } catch (error) {
                console.error('Error fetching JSON data:', error);

                this.showNotification('Error', '获取JSON数据时出现错误', 'error');
            }
        },
        showNotification(title, message, type) {
            ElNotification({
                title,
                message,
                type,
            });
        },
    },
};
</script>
  
<style scoped>
.container {
    margin: 20px auto;
}

.custom-input {
  margin: 20px auto;
  width: 480px;
}

.button-container {
    margin-top: 10px;
}

.result-table {
    margin-top: 20px;
}
</style>
  