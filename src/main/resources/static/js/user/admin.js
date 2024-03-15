const apiStatus = document.getElementById('api-status');

const apiRun1Button = document.getElementById('btnApiRun1');
const apiUrl1 = document.getElementById('apiUrl1');
const api1Param1 = document.getElementById('api1Param1');

apiRun1Button.addEventListener('click', function () {
    if (api1Param1.value.trim() === '') {
        alert('[state]를 입력해주세요.');
        $(api1Param1).focus();
        return;
    } else if (!regexUpperCase(api1Param1.value)) {
        alert('[state]는 영문 대문자만 입력가능합니다.');
        api1Param1.value = '';
        $(api1Param1).focus();
        return;
    }

    if (!confirm('작업을 실행하시겠습니까?'))
        return;

    apiStatus.innerHTML = '&#8987 작업중 - ' + apiUrl1.textContent;
    apiRun1Button.classList.add('btn-danger', 'label-mini-title', 'label-white');
    apiRun1Button.textContent = '작업중';
    apiRun1Button.disabled = 'true';
    apiRun2Button.style.display = 'none';
    apiRun3Button.style.display = 'none';
    apiRun4Button.style.display = 'none';
    api1Param1.disabled = 'true';

    $.ajax({
        type: 'GET',
        url: apiUrl1.textContent,
        data: {
            state: api1Param1.value
        },
        success: function () {
            alert('정상 처리되었습니다.');
            location.reload();
        }
    });
});

///

const apiRun2Button = document.getElementById('btnApiRun2');
const apiUrl2 = document.getElementById('apiUrl2');
const api2Param1 = document.getElementById('api2Param1');
const api2Param2 = document.getElementById('api2Param2');

apiRun2Button.addEventListener('click', function () {
    if (api2Param1.value.trim() === '') {
        alert('[start]를 입력해주세요.');
        $(api2Param1).focus();
        return;
    } else if (!regexDigit(api2Param1.value)) {
        alert('[start]는 숫자만 입력가능합니다.');
        api2Param1.value = '';
        $(api2Param1).focus();
        return;
    }

    if (api2Param2.value.trim() === '') {
        alert('[end]를 입력해주세요.');
        $(api2Param2).focus();
        return;
    } else if (!regexDigit(api2Param2.value)) {
        alert('[end]는 숫자만 입력가능합니다.');
        api2Param2.value = '';
        $(api2Param2).focus();
        return;
    }

    if (parseInt(api2Param1.value) > parseInt(api2Param2.value)) {
        alert('[start]는 [end]보다 클 수 없습니다.');
        api2Param1.value = '';
        api2Param2.value = '';
        $(api2Param1).focus();
        return;
    }

    apiStatus.innerHTML = '&#8987 작업중 - ' + apiUrl2.textContent;
    apiRun2Button.classList.add('btn-danger', 'label-mini-title', 'label-white');
    apiRun2Button.textContent = '작업중';
    apiRun2Button.disabled = 'true';
    apiRun1Button.style.display = 'none';
    apiRun3Button.style.display = 'none';
    apiRun4Button.style.display = 'none';
    api2Param1.enabled = 'false';
    api2Param2.enabled = 'false';

    $.ajax({
        type: 'GET',
        url: apiUrl2.textContent,
        data: {
            start: api2Param1.value,
            end: api2Param2.value
        },
        success: function () {
            alert('정상 처리되었습니다.');
            location.reload();
        }
    });
});

///

const apiRun3Button = document.getElementById('btnApiRun3');
const apiUrl3 = document.getElementById('apiUrl3');
const api3Param1 = document.getElementById('api3Param1');
const api3Param2 = document.getElementById('api3Param2');

apiRun3Button.addEventListener('click', function () {
    if (api3Param1.value.trim() === '') {
        alert('[start]를 입력해주세요.');
        $(api3Param1).focus();
        return;
    } else if (!regexDigit(api3Param1.value)) {
        alert('[start]는 숫자만 입력가능합니다.');
        api3Param1.value = '';
        $(api3Param1).focus();
        return;
    }

    if (api3Param2.value.trim() === '') {
        alert('[end]를 입력해주세요.');
        $(api3Param2).focus();
        return;
    } else if (!regexDigit(api3Param2.value)) {
        alert('[end]는 숫자만 입력가능합니다.');
        api3Param2.value = '';
        $(api3Param2).focus();
        return;
    }

    if (parseInt(api3Param1.value) > parseInt(api3Param2.value)) {
        alert('[start]는 [end]보다 클 수 없습니다.');
        api3Param1.value = '';
        api3Param2.value = '';
        $(api3Param1).focus();
        return;
    }

    apiStatus.innerHTML = '&#8987 작업중 - ' + apiUrl3.textContent;
    apiRun3Button.classList.add('btn-danger', 'label-mini-title', 'label-white');
    apiRun3Button.textContent = '작업중';
    apiRun3Button.disabled = 'true';
    apiRun1Button.style.display = 'none';
    apiRun2Button.style.display = 'none';
    apiRun4Button.style.display = 'none';
    api3Param1.enabled = 'false';
    api3Param2.enabled = 'false';

    $.ajax({
        type: 'GET',
        url: apiUrl3.textContent,
        data: {
            start: api3Param1.value,
            end: api3Param2.value
        },
        success: function () {
            alert('정상 처리되었습니다.');
            location.reload();
        }
    });
});

///

const apiRun4Button = document.getElementById('btnApiRun4');
const apiUrl4 = document.getElementById('apiUrl4');
const api4Param1 = document.getElementById('api4Param1');
const api4Param2 = document.getElementById('api4Param2');

apiRun4Button.addEventListener('click', function () {
    if (api4Param1.value.trim() === '') {
        alert('[start]를 입력해주세요.');
        $(api4Param1).focus();
        return;
    } else if (!regexDigit(api4Param1.value)) {
        alert('[start]는 숫자만 입력가능합니다.');
        api4Param1.value = '';
        $(api4Param1).focus();
        return;
    }

    if (api4Param2.value.trim() === '') {
        alert('[end]를 입력해주세요.');
        $(api4Param2).focus();
        return;
    } else if (!regexDigit(api4Param2.value)) {
        alert('[end]는 숫자만 입력가능합니다.');
        api4Param2.value = '';
        $(api4Param2).focus();
        return;
    }

    if (parseInt(api4Param1.value) > parseInt(api4Param2.value)) {
        alert('[start]는 [end]보다 클 수 없습니다.');
        api4Param1.value = '';
        api4Param2.value = '';
        $(api4Param1).focus();
        return;
    }

    apiStatus.innerHTML = '&#8987 작업중 - ' + apiUrl4.textContent;
    apiRun4Button.classList.add('btn-danger', 'label-mini-title', 'label-white');
    apiRun4Button.textContent = '작업중';
    apiRun4Button.disabled = 'true';
    apiRun1Button.style.display = 'none';
    apiRun2Button.style.display = 'none';
    apiRun3Button.style.display = 'none';
    api4Param1.disabled = 'true';
    api4Param2.disabled = 'true';

    $.ajax({
        type: 'GET',
        url: apiUrl4.textContent,
        data: {
            start: api4Param1.value,
            end: api4Param2.value
        },
        success: function () {
            alert('정상 처리되었습니다.');
            location.reload();
        }
    });
});

///

function regexDigit(param) {
    const regex = /^[0-9]*$/;
    return regex.test(param);
}

function regexUpperCase(param) {
    const regex = /^[A-Z]*$/;
    return regex.test(param);
}

///

function saveUser(idx) {
    let roles = [];

    let i = 0;
    for (i; i<8; i++) {
        const e = document.getElementById('chk-' + idx + '-' + i);
        if (e.checked) {
            const l = document.getElementById('lbl-' + idx + '-' + i);
            roles.push(l.textContent.trim());
        }
    }

    const id = document.getElementById('user-id-' + idx);

    $.ajax({
        type: 'POST',
        url: '/admin/user',
        data: JSON.stringify({
            userId: id.textContent,
            userRoles: roles
        }),
        contentType: 'application/json; charset=utf-8',
        success: function () {
            alert('저장되었습니다.');
        },
        error: function() {
            alert('저장 중 오류가 발생했습니다.');
        }
    });
}
