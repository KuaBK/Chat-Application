'use strict';

const usernamePage = document.querySelector('#username-page');
const chatPage = document.querySelector('#chat-page');
const usernameForm = document.querySelector('#usernameForm');
const messageForm = document.querySelector('#messageForm');
const messageInput = document.querySelector('#message');
const connectingElement = document.querySelector('.connecting');
const chatArea = document.querySelector('#chat-messages');
const logout = document.querySelector('#logout');
const profileBtn = document.getElementById('profile-btn');
const profileMenu = document.getElementById('profile-menu');
const viewProfile = document.getElementById('view-profile');
const logoutBtn = document.getElementById('logout');
const registerPage = document.querySelector('#register-page');
const registerForm = document.querySelector('#registerForm');
const showRegister = document.querySelector('#showRegister');
const showLogin = document.querySelector('#showLogin');

let stompClient = null;
let nickname = null;
let fullname = null;
let selectedUserId = null;

const loginPage = document.querySelector('#login-page');
const loginForm = document.querySelector('#loginForm');

let token = null; // Store JWT token globally

showRegister.addEventListener('click', () => {
    loginPage.classList.add('hidden');
    registerPage.classList.remove('hidden');
});

registerForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const nickname = document.querySelector('#reg-nickname').value;
    const fullName = document.querySelector('#reg-fullname').value;
    const email = document.querySelector('#reg-email').value;
    const password = document.querySelector('#reg-password').value;

    const response = await fetch('/account/register', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            fullName: fullName,
            email: email,
            password: password
        })
    });

    if (response.ok) {
        alert("Register successful! Please login.");
        registerPage.classList.add('hidden');
        loginPage.classList.remove('hidden');
    } else {
        const err = await response.json();
        alert("Register failed: " + (err.message || "Unknown error"));
    }
});

showLogin.addEventListener('click', () => {
    registerPage.classList.add('hidden');
    loginPage.classList.remove('hidden');
});

loginForm.addEventListener('submit', async function (event) {
    event.preventDefault();

    const email = document.querySelector('#email').value;
    const password = document.querySelector('#password').value;

    try {
        const response = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!response.ok) {
            const error = await response.json();
            alert(error.message || 'Login failed');
            return;
        }

        const data = await response.json();
        const token = data.result.token;

        // Giải mã payload từ token JWT
        const payloadBase64 = token.split('.')[1];
        const payload = JSON.parse(atob(payloadBase64));
        const emailFromToken = payload.sub;

        // Gọi API để lấy user info
        const userInfoRes = await fetch(`/account/${emailFromToken}`);

        if (!userInfoRes.ok) {
            alert('Failed to fetch user info');
            return;
        }

        const userInfo = await userInfoRes.json();
        nickname = userInfo.nickName;
        fullname = userInfo.fullName;

        // Giấu trang login và trực tiếp vào trang chat
        loginPage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        // Kết nối WebSocket và tiếp tục như bình thường
        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, onConnected, onError);

    } catch (err) {
        console.error(err);
        alert('An error occurred during login');
    }
});



function connect(event) {
    nickname = document.querySelector('#nickname').value.trim();
    fullname = document.querySelector('#fullname').value.trim();

    if (nickname && fullname) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        const socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    stompClient.subscribe(`/user/${nickname}/queue/messages`, onMessageReceived);
    stompClient.subscribe(`/user/public`, onMessageReceived);

    // register the connected user
    stompClient.send("/app/user.addUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'ONLINE'})
    );
    document.querySelector('#connected-user-fullname').textContent = fullname;
    findAndDisplayConnectedUsers().then();
}

async function findAndDisplayConnectedUsers() {
    const usersResponse = await fetch('/users/all', {
        headers: { Authorization: `Bearer ${token}` }
    });

    let users = await usersResponse.json();

    users = users.filter(user => user.nickName !== nickname && user.fullName !== fullname);

    const connectedUsersList = document.getElementById('connectedUsers');
    connectedUsersList.innerHTML = '';

    users.forEach(user => {
        appendUserElement(user, connectedUsersList);
        if (users.indexOf(user) < users.length - 1) {
            const separator = document.createElement('li');
            separator.classList.add('separator');
            connectedUsersList.appendChild(separator);
        }
    });
}

function appendUserElement(user, connectedUsersList) {
    const listItem = document.createElement('li');
    listItem.classList.add('user-item');
    listItem.id = user.nickName;

    const userImage = document.createElement('img');
    userImage.src = '../img/user_icon.png';
    userImage.alt = user.fullName;

    const usernameSpan = document.createElement('span');
    usernameSpan.textContent = user.fullName;

    const receivedMsgs = document.createElement('span');
    receivedMsgs.textContent = '0';
    receivedMsgs.classList.add('nbr-msg', 'hidden');

    const statusSpan = document.createElement('span');
    statusSpan.textContent = user.status === 'ONLINE' ? '🟢' : '⚪';
    statusSpan.classList.add('user-status');

    listItem.appendChild(userImage);
    listItem.appendChild(usernameSpan);
    listItem.appendChild(statusSpan);
    listItem.appendChild(receivedMsgs);

    listItem.addEventListener('click', userItemClick);

    connectedUsersList.appendChild(listItem);
}


function userItemClick(event) {
    document.querySelectorAll('.user-item').forEach(item => {
        item.classList.remove('active');
    });
    messageForm.classList.remove('hidden');

    const clickedUser = event.currentTarget;
    clickedUser.classList.add('active');

    selectedUserId = clickedUser.getAttribute('id');
    fetchAndDisplayUserChat().then();

    clickedUser.classList.remove('new-message');

    const nbrMsg = clickedUser.querySelector('.nbr-msg');
    nbrMsg.classList.add('hidden');
    nbrMsg.textContent = '0';
}

function displayMessage(senderId, content) {
    const messageContainer = document.createElement('div');
    messageContainer.classList.add('message');
    if (senderId === nickname) {
        messageContainer.classList.add('sender');
    } else {
        messageContainer.classList.add('receiver');
    }
    const message = document.createElement('p');
    message.textContent = content;
    messageContainer.appendChild(message);
    chatArea.appendChild(messageContainer);
}

async function fetchAndDisplayUserChat() {
    const userChatResponse = await fetch(`/messages/${nickname}/${selectedUserId}`);
    const userChat = await userChatResponse.json();
    chatArea.innerHTML = '';
    userChat.forEach(chat => {
        displayMessage(chat.senderId, chat.content);
    });
    chatArea.scrollTop = chatArea.scrollHeight;
}


function onError() {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    const messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        const chatMessage = {
            senderId: nickname,
            recipientId: selectedUserId,
            content: messageInput.value.trim(),
            timestamp: new Date()
        };
        stompClient.send("/app/chat", {}, JSON.stringify(chatMessage));
        displayMessage(nickname, messageInput.value.trim());
        messageInput.value = '';
    }
    chatArea.scrollTop = chatArea.scrollHeight;
    event.preventDefault();
}


async function onMessageReceived(payload) {
    await findAndDisplayConnectedUsers();
    console.log('Message received', payload);
    const message = JSON.parse(payload.body);
    if (selectedUserId && selectedUserId === message.senderId) {
        displayMessage(message.senderId, message.content);
        chatArea.scrollTop = chatArea.scrollHeight;
    }

    if (selectedUserId) {
        document.querySelector(`#${selectedUserId}`).classList.add('active');
    } else {
        messageForm.classList.add('hidden');
    }

    const notifiedUser = document.querySelector(`#${message.senderId}`);
    if (notifiedUser && !notifiedUser.classList.contains('active')) {
        const nbrMsg = notifiedUser.querySelector('.nbr-msg');
        nbrMsg.classList.remove('hidden');
        nbrMsg.textContent = '';

        let currentCount = parseInt(nbrMsg.textContent) || 0;
        nbrMsg.textContent = (currentCount + 1).toString();

        notifiedUser.classList.add('new-message');
    }
}

function onLogout() {
    stompClient.send("/app/user.disconnectUser",
        {},
        JSON.stringify({nickName: nickname, fullName: fullname, status: 'OFFLINE'})
    );
    window.location.reload();
}

profileBtn.addEventListener('click', (event) => {
    profileMenu.classList.toggle('hidden');
    event.stopPropagation();  // Ngăn chặn sự kiện lan ra ngoài
});

document.addEventListener('click', (event) => {
    if (!profileMenu.contains(event.target) && event.target !== profileBtn) {
        profileMenu.classList.add('hidden');
    }
});

viewProfile.addEventListener('click', () => {
    alert("Xem thông tin cá nhân!");
    profileMenu.classList.add('hidden');
});

logoutBtn.addEventListener('click', () => {
    onLogout();  // Gọi hàm logout có sẵn
});

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', sendMessage, true);
logout.addEventListener('click', onLogout, true);
window.onbeforeunload = () => onLogout();


