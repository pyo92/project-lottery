/**
 * load 시, 초기 설정 handler
 */
window.addEventListener('load', function () {
    const idInput = document.getElementById('id');

    validateId();
    validatePassword();

    idInput.focus();

    //alert 표시 후, 삭제
    const serverAlert = document.querySelector('.alert.alert-danger');
    if (serverAlert != null) {
        alert(serverAlert.textContent.trim());
        serverAlert.remove();
    }
});

//////////////////////////////

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

idInput.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') { // Enter 키를 눌렀을 때
        e.preventDefault(); // 폼 제출을 막음
        passwordInput.focus(); // 비밀번호 필드로 포커스 이동
    }
})

/**
 * id 입력값 validation
 */
function validateId() {
    const id = idInput.value.trim();
    if (id === '') {
        setError(idInput, idErrorMessage, '아이디를 입력해주세요.');
    } else {
        clearError(idInput, idErrorMessage);
    }
}

//////////////////////////////

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

/**
 * password 입력값 validation
 */
function validatePassword() {
    const password = passwordInput.value.trim();
    if (password === '') {
        setError(passwordInput, passwordErrorMessage, '비밀번호를 입력해주세요.');
    } else {
        clearError(passwordInput, passwordErrorMessage);
    }
}

//////////////////////////////

/**
 * validation error message 표시
 */
function setError(inputElement, errorMessageElement, message) {
    inputElement.classList.add('is-invalid');
    errorMessageElement.style.display = 'inline';
    errorMessageElement.textContent = message;
}

/**
 * validation error message 삭제
 */
function clearError(inputElement, errorMessageElement) {
    inputElement.classList.remove('is-invalid');
    inputElement.classList.add('is-valid');
    errorMessageElement.style.display = 'none';
}

//////////////////////////////

const spinnerContainer = document.querySelector('.spinner-container');
const loginForm = document.querySelector('.dh-login');
let keyDownPrevent; //submit 도중 키 입력 막기 위한 flag

/**
 * submit event handler
 */
loginForm.addEventListener('submit', function (e) {
    const id = idInput.value.trim();
    const password = passwordInput.value.trim();
    if (id === '') {
        e.preventDefault();
        validateId();
        alert('아이디를 입력해주세요.')
        idInput.focus();
        return;
    }

    if (password === '') {
        e.preventDefault();
        validatePassword();
        alert('비밀번호를 입력해주세요.')
        passwordInput.focus();
        return;
    }

    if (!enterIndexes.includes(currentRangeIndex)) {
        e.preventDefault();
        alert('판매 시간이 아닙니다.')
        return;
    }

    //키 입력 방지 flag 설정 및 spinner 표시
    keyDownPrevent = true;
    spinnerContainer.style.display = 'flex';
});

/**
 * keydown event handler
 */
document.addEventListener('keydown', function (e) {
    if (keyDownPrevent) {
        e.preventDefault();
    }
});
