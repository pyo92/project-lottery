window.addEventListener('load', function () {
    const idInput = document.getElementById('id');
    validateId();
    validatePassword();
    idInput.focus();

    const serverAlert = document.querySelector('.alert.alert-danger');

    if (serverAlert != null) {
        const currentDate = new Date();
        const currentTime = currentDate.toLocaleTimeString(); // 현재 시간을 문자열로 변환
        const milliseconds = currentDate.getMilliseconds().toString().padStart(3, '0'); // 밀리초 추출
        const alertMessage = document.createElement('span');
        alertMessage.className = 'fas fa-triangle-exclamation';
        alertMessage.textContent = ` [${currentTime}.${milliseconds}] - ${serverAlert.textContent}`;
        serverAlert.textContent = '';
        serverAlert.appendChild(alertMessage);
        serverAlert.style.display = 'block'; // 알림 창 표시
        setTimeout(function () {
            serverAlert.remove();
        }, 5000);
    }
});

//////////

const idInput = document.getElementById('id');
const idErrorMessage = document.getElementById('id-error');

idInput.addEventListener('focusin', function (e) {
    validateId();
});

idInput.addEventListener('blur', function (e) {
    validateId();
});

idInput.addEventListener('input', function (e) {
    validateId();
});

function validateId() {
    const id = idInput.value.trim();
    if (id === '') {
        setError(idInput, idErrorMessage, '아이디를 입력해주세요.');
    } else {
        clearError(idInput, idErrorMessage);
    }
}

const passwordInput = document.getElementById('password');
const passwordErrorMessage = document.getElementById('password-error');

passwordInput.addEventListener('focusin', function (e) {
    validatePassword();
});

passwordInput.addEventListener('blur', function (e) {
    validatePassword();
});

passwordInput.addEventListener('input', function (e) {
    validatePassword();
});

function validatePassword() {
    const password = passwordInput.value.trim();
    if (password === '') {
        setError(passwordInput, passwordErrorMessage, '비밀번호를 입력해주세요.');
    } else {
        clearError(passwordInput, passwordErrorMessage);
    }
}

function setError(inputElement, errorMessageElement, message) {
    inputElement.classList.add('is-invalid');
    errorMessageElement.style.display = 'inline';
    errorMessageElement.textContent = message;
}

function clearError(inputElement, errorMessageElement) {
    inputElement.classList.remove('is-invalid');
    inputElement.classList.add('is-valid');
    errorMessageElement.style.display = 'none';
}

//////////

const MAX_ALERTS = 3; // 최대 알림 창 개수
const loginAlertContainer = document.querySelector('.alert-container');

function showAlert(message) {
    const currentDate = new Date();
    const currentTime = currentDate.toLocaleTimeString(); // 현재 시간을 문자열로 변환
    const milliseconds = currentDate.getMilliseconds().toString().padStart(3, '0'); // 밀리초 추출
    const alertMessage = document.createElement('span');
    alertMessage.className = 'fas fa-triangle-exclamation';
    alertMessage.textContent = ` [${currentTime}.${milliseconds}] - ${message}`;

    const newAlert = document.createElement('div'); // 새로운 알림 창 생성
    newAlert.className = 'alert alert-warning label-bold-wrap-text'; // CSS 클래스 설정
    newAlert.appendChild(alertMessage);
    newAlert.style.display = 'block'; // 알림 창 표시

    newAlert.addEventListener('click', function () {
        this.remove();
    });

    // 최대 알림 창 개수 제한
    if (loginAlertContainer.children.length >= MAX_ALERTS) {
        loginAlertContainer.removeChild(loginAlertContainer.lastElementChild);
    }

    loginAlertContainer.insertBefore(newAlert, loginAlertContainer.firstChild); // 문서에 추가

    setTimeout(function () {
        newAlert.remove();
    }, 5000);
}

//////////

const spinnerContainer = document.querySelector('.spinner-container');
const loginForm = document.querySelector('.dh-login');
let keyDownPrevent; //submit 후, spinner 출력 중일 때, 키 입력 막기 위한 flag

loginForm.addEventListener('submit', function (e) {
    const id = idInput.value.trim();
    const password = passwordInput.value.trim();
    if (id === '') {
        e.preventDefault();
        validateId();
        showAlert('아이디를 입력해주세요.')
        return;
    }

    if (password === '') {
        e.preventDefault();
        validatePassword();
        showAlert('비밀번호를 입력해주세요.')
        return;
    }

    if (!enterIndexes.includes(currentRangeIndex)) {
        e.preventDefault();
        showAlert('지금은 구매할 수 없습니다.')
        return;
    }

    keyDownPrevent = true;
    spinnerContainer.style.display = 'flex';
});

document.addEventListener('keydown', function (e) {
    if (keyDownPrevent) {
        e.preventDefault();
    }
});
