<template>
  <div class="home">
    <h1>AV Evasion Craft Online</h1>

    <div class="logo-section">
      <img src="@/assets/logo1.png" alt="Logo" class="custom-logo" />
    </div>

    <div class="compiler-container">
      <div class="button-section">
        <el-button v-for="button in buttons" :key="button.id" @click="showElement(button.id)">
          {{ button.label }}
        </el-button>

      </div>
      <div class="main-content">
        <div v-if="elementToShow === 1" class="upload-section">
          <el-upload class="upload-demo" ref="upload" action="" drag :auto-upload="false" :show-file-list="true"
            method="post" :multiple="true" :http-request="handleShellcodeUpload" :limit="1" @change="handleChange">
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">
              将 bin 文件拖拽或 <em>点击这里上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                bin 限制 5 mb
              </div>
            </template>
          </el-upload>
          <div class="input-contain">
            <div class="select-container">

              <el-select class="select-container2" v-model="selectedLanguage" placeholder="模板选择">
                <el-option v-for="(templates, language) in templatesMapping" :key="language" :label="language"
                  :value="language">
                </el-option>
              </el-select>
              <el-select class="select-container2"  v-model="selectedTemplate.transformation" placeholder="转换方式选择">
                <el-option v-for="transformation in selectedLanguageTemplates.transformation" :key="transformation"
                  :label="transformation" :value="transformation">
                </el-option>
              </el-select>
              <el-select v-model="selectedTemplate.loadMethod" placeholder="shellcode存储位置">
                <el-option v-for="method in selectedLanguageTemplates.loadMethod" :key="method" :label="method"
                  :value="method">
                </el-option>
              </el-select>
            </div>
          </div>
          <div class="select-container">
            <el-input v-if="selectedTemplate.loadMethod === 'LOCAL' || selectedTemplate.loadMethod === 'REMOTE'"
              v-model="additionalParameter" style="width: 620px;"
              :placeholder="selectedTemplate.loadMethod === 'LOCAL' ? '本地资源名称' : '远程资源url例如：http://test/shellcode'"></el-input>
          </div>
          <div class="result-section">
            <el-button class="button-section" @click="handleUpload"
              :disabled="(selectedLanguage === '' || selectedTemplateName === '')">
              Compile
            </el-button>
            <el-button class="button-section" @click="handleDownload" :disabled="compilationResult === ''">
              Download
            </el-button>
          </div>
        </div>

        <div v-if="elementToShow === 3">
          <ProcessValueLookup />
        </div>

        <p v-if="elementToShow === 99" class="disclaimer">
          本工具仅供安全研究和教学目的使用，用户须自行承担因使用该工具而引起的一切法律及相关责任。作者概不对任何法律责任承担责任，且保留随时中止、修改或终止本工具的权利。使用者应当遵循当地法律法规，并理解并同意本声明的所有内容。
        </p>
      </div>
    </div>

    <el-footer class="footer">
      <p>&copy; 2024 在线免杀平台, powered by <a href="https://github.com/yutianqaq">Yutian</a></p>
    </el-footer>
  </div>
</template>

<script>
import ProcessValueLookup from '@/components/ProcessValueLookup.vue';
import { fetchDownloadLink, fetchCompileUpload, fetchConfig } from '../api/compile.js';
import { ElNotification, ElUpload, } from 'element-plus'

function showNotification(title, message, type) {
  ElNotification({
    title,
    message,
    type,
  });
}


export default {
  name: 'BypassAV',
  components: {
    ProcessValueLookup,
  },
  data() {
    return {
      code: '',
      elementToShow: 1,
      buttons: [
        { id: 1, label: 'bin 格式' },
        { id: 3, label: '杀软识别' },
        { id: 99, label: '免责声明' },
      ],
      // selectedLanguage: 'go',
      // selectedTemplate: '',
      // selectedTemplateName: '',
      // selectedTransformation: '',
      // selectedLoadMethod: '',
      // languages: {},
      templatesMapping: {},
      selectedLanguage: "",
      selectedTemplate: {
        loadMethod: "",
        transformation: ""
      },
      fileList: [],
      storageType: '',
      additionalParameter: '',
      compilationResult: '',
      elementToShow: 1,
    };
  },
  computed: {
    showSecondSelect() {
      return this.selectedLanguage.split('_')[0] === 'nim' || this.selectedLanguage.split('_')[0] === 'c' || this.selectedLanguage.split('_')[0] === 'golang';
    },
    selectedLanguageTemplates() {
      return this.templatesMapping[this.selectedLanguage] || {};
    },
  },
  watch: {
    selectedLanguage(newLanguage) {
      this.selectedTemplate = {
        loadMethod: "",
        transformation: "",
      };
    },
  },
  methods: {
    showElement(id) {
      this.elementToShow = id;
    },
    handleChange(file, fileList) {
      this.fileList = [...fileList];
    },
    handleUpload() {
      if (this.fileList.length === 0) {
        showNotification('Error', '请选择文件', 'error');
        return false;
      }
      this.$refs.upload.submit();
    },
    async loadLoaderOptions() {
      try {
        const response = await fetchConfig();
        this.templatesMapping = response.data.templatesMapping
      } catch (error) {
        console.error('Failed to load options:', error);
      }
    },
    async handleShellcodeUpload(file) {
      if (file) {
        showNotification('Success', '生成中，请等待下载按钮亮起', 'success');
      }
      var { file } = file;
      if (this.selectedLanguage.split('_')[0] === 'go' || this.selectedLanguage.split('_')[0] === 'c' || this.selectedLanguage.split('_')[0] === 'nim') {
        const formData = new FormData();
        formData.append('shellcode', file);
        formData.append('templateLanguage', this.selectedLanguage.split('_')[0]);
        formData.append('templateName', this.selectedLanguage);
        formData.append('transformation', this.selectedTemplate.transformation);
        formData.append('storageType', this.selectedTemplate.loadMethod);
        formData.append('additionalParameter', this.additionalParameter);

        try {
          const response = await fetchCompileUpload(formData, {
            headers: {
              'Content-Type': 'multipart/form-data',
            },
          });
          this.compilationResult = response.data.data.downloadLink;
        } catch (error) {
          console.error('Error:', error);
          showNotification('Error', '生成失败...', 'error');
        }
        this.resetAllOption();
        return true;
      } else {
        showNotification('Error', '生成失败...', 'success');
      }
    },
    async handlecompilationResult() {
      try {
        const response = await fetchDownloadLink(this.compilationResult);
        const contentDisposition = response.headers['content-disposition'];

        const blob = new Blob([response.data]);
        let filename = 'fail.bin'; // Default filename https://github.com/yutianqaq

        if (contentDisposition) {
          const matches = contentDisposition.match(/filename="(.+)"/);
          if (matches && matches.length > 1) {
            filename = matches[1];
          }
        }

        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = filename;

        document.body.appendChild(link);

        await new Promise(resolve => setTimeout(resolve, 100));

        link.click();
        document.body.removeChild(link);
        URL.revokeObjectURL(url);
        this.compilationResult = '';
        history.go(0)
      } catch (error) {
        console.error('Error fetching or processing download data:', error);
      }
    },
    handleDownload() {
      if (this.compilationResult) {
        this.handlecompilationResult();
      } else {
        console.warn('No compilation result available for download.');
      }
    },
    resetSecondOption() {
      this.selectedTemplate = '';
      this.storageType = '';
    },
    resetAllOption() {
      this.selectedLanguage = '';
      this.selectedTemplate = '';
      this.storageType = '';
    }
  },
  mounted() {
    this.loadLoaderOptions();
  },
};
</script>

<style scoped>
.home {
  text-align: center;
}

h1 {
  margin-bottom: 20px;
}

.logo-section {
  position: relative;
}

.custom-logo {
  position: fixed;
  top: 67px;
  left: 81px;
  width: 100px;
  height: auto;
  border-radius: 10px;
}

.compiler-container {
  max-width: 800px;
  margin: 0 auto;
}

.main-content {
  position: relative;
  height: 550px;
}


.button-section {
  margin-bottom: 20px;
}

.upload-section {
  margin-bottom: 40px;
}

.input-contain {
  display: flex;
  align-items: center;
}

.select-container {
  margin: 0 auto;
  display: block;
  width: 680px;
}

.select-container2 {
  margin-right: 10px;
}

.el-select,
.el-input {
  width: 48%;
  margin-bottom: 10px;
  width: 200px;
}

.result-section {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
}

.footer {
  position: fixed;
  left: 0;
  bottom: 0;
  width: 100%;
  padding: 20px;
  text-align: center;
}
</style>