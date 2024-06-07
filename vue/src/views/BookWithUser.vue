<template>
  <div class="chat-container">
    <h1 class="chat-title">智能帮你找书</h1>
    <p class="chat-subtitle">请描述你的想要阅读的图书，我将帮你推荐</p>
    <div class="chat-box">
      <div v-for="message in messages" :key="message.id" :class="['message', message.sender === '我' ? 'my-message' : 'other-message']">
        <strong>{{ message.sender }}:</strong> <span>{{ message.content }}</span>
      </div>
    </div>
    <div class="input-container">
      <input v-model="newMessage" @keyup.enter="sendChat" placeholder="输入消息...">
      <button @click="sendChat">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const messages = ref([]);
const newMessage = ref('');

async function sendChat() {
  if (!newMessage.value.trim()) return;
  const message = {
    id: Date.now(),
    sender: '我',
    content: newMessage.value
  };
  messages.value.push(message);

  try {
    const response = await axios.post('http://localhost:9090/api/vivogpt', { prompt: newMessage.value });
    messages.value.push({
      id: Date.now(),
      sender: '北大软微',
      content: response.data
    });
  } catch (error) {
    console.error('Error:', error);
    messages.value.push({
      id: Date.now(),
      sender: '系统',
      content: '无法从服务器获取回答，请检查控制台了解详细信息。'
    });
  }

  newMessage.value = '';
}
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  max-width: 600px;
  margin: 20px auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f2f2f2;
}

.chat-title {
  text-align: center;
  margin-bottom: 5px;
  font-size: 24px;
  color: #333;
}

.chat-subtitle {
  text-align: center;
  margin-bottom: 20px;
  font-size: 16px;
  color: #666;
}

.chat-box {
  flex-grow: 1;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 8px;
  background-color: #e6e6f1;
  max-height: 500px;
  overflow-y: auto;
  margin-bottom: 10px;
}

.message {
  padding: 10px;
  margin: 5px 0;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
}

.my-message {
  align-self: flex-end;
  background-color: #d1ecf1;
  border: 1px solid #bee5eb;
}

.other-message {
  align-self: flex-start;
  background-color: #fff3cd;
  border: 1px solid #ffeeba;
}

.input-container {
  display: flex;
  align-items: center;
  margin-top: 10px;
}

input {
  flex-grow: 1;
  padding: 10px;
  margin-right: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:hover {
  background-color: #0056b3;
}
</style>
