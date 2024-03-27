/**
 * 사용자 탭 관련
 */

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

////////////////////////////////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////////////

/**
 * API 탭 관련
 */

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

////////////////////////////////////////////////////////////////////////////////////

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

//TODO: api 탭을 refresh 하는 함수를 만들어서 해당 탭에서도 편하게 쓰고, selenium 탭에서도 쓰자.

function regexDigit(param) {
    const regex = /^[0-9]*$/;
    return regex.test(param);
}

function regexUpperCase(param) {
    const regex = /^[A-Z]*$/;
    return regex.test(param);
}

////////////////////////////////////////////////////////////////////////////////////

/**
 * Selenium 탭 관련
 */

const usages = ['scrap', 'purchase'];

const btnRefreshSeleniumTab = document.getElementById('btn-refresh-selenium-tab');
btnRefreshSeleniumTab.addEventListener('click', refreshSeleniumTab);

/**
 * Selenium 새로고침 버튼 처리 method
 */
function refreshSeleniumTab() {
    $.ajax({
        type: 'GET',
        url: '/admin/selenium',
        success: function (data) {
            makeSeleniumTab(data);
        }
    });
}

/**
 * Selenium 탭 view 생성용 method
 * @param data Selenium driver 목록 정보 by ajax
 */
function makeSeleniumTab(data) {
    const cntSpan = document.getElementById('selenium-cnt');
    cntSpan.innerHTML = '&#9989 ' + data.length + '개';

    const grid = document.getElementById('selenium-grid');
    while (grid.firstChild) {
        grid.removeChild(grid.firstChild); //기존의 하위 목록 삭제
    }

    $.each(data, function(index, item) {
        const unitDiv = document.createElement('div');
        unitDiv.className = 'selenium';

        const infoDiv = document.createElement('div');
        infoDiv.className = 'selenium-info';

        const detailDiv = document.createElement('div');
        detailDiv.className = 'selenium-info-detail';

        const headerDiv = document.createElement('div');
        headerDiv.className = 'selenium-info-detail-header';

        const nameDiv = document.createElement('div');
        nameDiv.className = 'selenium-name';

        const idxSpan = document.createElement('span');
        idxSpan.className = 'label-mini-title';
        idxSpan.innerHTML = '[' + (index + 1) + ']&nbsp;'

        const nameSpan = document.createElement('span');
        nameSpan.className = 'label-bold-ellipsis-text';
        nameSpan.textContent = item.name;

        const suspendBtn = document.createElement('button');
        suspendBtn.classList.add('btn', 'btn-danger', 'label-mini-title', 'label-white');
        suspendBtn.id = 'btnSelenium' + (index + 1);
        suspendBtn.style.display = item.isRunning ? 'block' : 'none';
        suspendBtn.onclick = () => {
            suspendSeleniumProcess(index); //usages 는 배열이므로, index 증가없이 사용한다.
        }
        suspendBtn.textContent = '중지';

        const usageSpan = document.createElement('span');
        usageSpan.classList.add('label-mini-title', 'selenium-description');
        usageSpan.innerHTML = '&#9654;&nbsp; ' + (item.name.endsWith('scrap') ? '동행복권 정보 스크랩핑' : '동행복권 로그인 & 구매');

        const hr = document.createElement('hr');
        hr.className = 'no-margin-hr';

        const statusDiv = document.createElement('div');
        statusDiv.className = 'selenium-status';

        const statusSpan = document.createElement('span');
        statusSpan.className = 'label-mini-title';
        statusSpan.id = 'lblSeleniumStatus' + (index + 1);
        statusSpan.innerHTML = item.isRunning ? '&#8987;&nbsp;작업중' : '&#128164;&nbsp;유휴 상태';

        statusDiv.appendChild(statusSpan);

        nameDiv.appendChild(idxSpan);
        nameDiv.appendChild(nameSpan);

        headerDiv.appendChild(nameDiv);
        headerDiv.appendChild(suspendBtn);

        detailDiv.appendChild(headerDiv);
        detailDiv.appendChild(usageSpan);

        infoDiv.appendChild(detailDiv);

        unitDiv.appendChild(infoDiv);
        unitDiv.appendChild(hr);
        unitDiv.appendChild(statusDiv);

        grid.appendChild(unitDiv);
    });
}

/**
 * Selenium driver 작업 중지 method
 * @param idx Driver index
 */
function suspendSeleniumProcess(idx) {
    $.ajax({
        type: 'POST',
        url: '/admin/selenium',
        data: {
            usage: usages[idx]
        },
        success: function () {
            alert('정상적으로 중지되었습니다.');
        },
        error: function () {
            alert('작업중 오류가 발생했습니다.');
        }
    });

    refreshSeleniumTab();
    //TODO: api 탭 새로고침 method 분리하면, 그때 여기 동작시키도록 한다. (둘은 서로 유기적 관계)
}

////////////////////////////////////////////////////////////////////////////////////

/**
 * Redis 탭 관련
 */

const btnRefreshRedisTab = document.getElementById('btn-refresh-redis-tab');
btnRefreshRedisTab.addEventListener('click', refreshRedisTab);

/**
 * Redis 새로고침 버튼 처리 method
 */
function refreshRedisTab() {
    $.ajax({
        type: 'GET',
        url: '/admin/redis',
        success: function (data) {
            makeRedisTab(data);
        }
    });
}

/**
 * Redis 탭 view 생성용 method
 * @param data Redis cache 목록 정보 by ajax
 */
function makeRedisTab(data) {
    const cntSpan = document.getElementById('redis-cnt');
    cntSpan.innerHTML = '&#128273 ' + data.length + '개';

    const grid = document.getElementById('redis-grid');
    while (grid.firstChild) {
        grid.removeChild(grid.firstChild); //기존의 하위 목록 삭제
    }

    $.each(data, function(index, item) {
        const unitDiv = document.createElement('div');
        unitDiv.className = 'redis';

        const infoDiv = document.createElement('div');
        infoDiv.className = 'redis-info';

        const detailDiv = document.createElement('div');
        detailDiv.className = 'redis-info-detail';

        const headerDiv = document.createElement('div');
        headerDiv.className = 'redis-info-detail-header';

        const nameDiv = document.createElement('div');
        nameDiv.className = 'redis-name';

        const idxSpan = document.createElement('span');
        idxSpan.className = 'label-mini-title';
        idxSpan.innerHTML = '[' + (index + 1) + ']&nbsp;'

        const nameSpan = document.createElement('span');
        nameSpan.className = 'label-bold-ellipsis-text';
        nameSpan.textContent = item.key;

        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('btn', 'btn-danger', 'label-mini-title', 'label-white');
        deleteBtn.id = 'btnRedis' + (index + 1);
        deleteBtn.onclick = () => {
            deleteRedisCache(item.type, item.key);
        }
        deleteBtn.textContent = '삭제';

        const setDiv = document.createElement('div');
        setDiv.className = 'redis-set-type';
        setDiv.style.display = item.type.toString() === 'STRING' ? 'block' : 'none';

        const setSpan1 = document.createElement('span');
        setSpan1.className = 'label-mini-title';
        setSpan1.innerHTML = '&#9654;&nbsp;&nbsp;value =&nbsp;';

        const setSpan2 = document.createElement('span');
        setSpan2.classList.add('label-mini-title', 'label-special');
        setSpan2.textContent = item.value;

        setDiv.appendChild(setSpan1);
        setDiv.appendChild(setSpan2);

        const zsetDiv = document.createElement('div');
        zsetDiv.className = 'redis-zset-type';
        zsetDiv.style.display = item.type.toString() === 'ZSET' ? 'block' : 'none';

        const zsetSpan1 = document.createElement('span');
        zsetSpan1.className = 'label-mini-title';
        zsetSpan1.innerHTML = '&#9654;&nbsp;&nbsp;range =&nbsp;';

        const zsetSpan2 = document.createElement('span');
        zsetSpan2.classList.add('label-mini-title', 'label-special');
        zsetSpan2.textContent = '1 ... ' + item.zSetCnt;

        zsetDiv.appendChild(zsetSpan1);
        zsetDiv.appendChild(zsetSpan2);

        const hsetDiv = document.createElement('div');
        hsetDiv.className = 'redis-hset-type';
        hsetDiv.style.display = item.type.toString() === 'HASH' ? 'block' : 'none';

        const hsetSpan = document.createElement('span');
        hsetSpan.className = 'label-mini-title';
        hsetSpan.innerHTML = '&#9654;&nbsp;&nbsp;fields';

        const hsetFieldsDiv = document.createElement('div');
        hsetFieldsDiv.className = 'hset-fields';

        $.each(item.hSetFields, function(idx, itm) {
            const hsetFieldDiv = document.createElement('div');
            hsetFieldDiv.className = 'hset-field';

            const hsetFieldDelBtn = document.createElement('button');
            hsetFieldDelBtn.classList.add('btn', 'btn-danger', 'btn-delete');
            hsetFieldDelBtn.onclick = () => {
                deleteRedisCacheForHashSetField(item.type, item.key, itm);
            }

            const hsetFieldDelBtnSpan = document.createElement('span');
            hsetFieldDelBtnSpan.classList.add('fas', 'fa-trash', 'label-mini-title', 'label-white');

            hsetFieldDelBtn.appendChild(hsetFieldDelBtnSpan);

            const hsetFieldNameDiv = document.createElement('div');
            hsetFieldNameDiv.className = 'field-name';

            const hsetFieldNameSpan = document.createElement('span');
            hsetFieldNameSpan.classList.add('label-mini-title', 'label-special');
            hsetFieldNameSpan.textContent = itm;

            hsetFieldNameDiv.appendChild(hsetFieldNameSpan);

            hsetFieldDiv.appendChild(hsetFieldDelBtn);
            hsetFieldDiv.appendChild(hsetFieldNameDiv);

            hsetFieldsDiv.appendChild(hsetFieldDiv);
        });

        hsetDiv.appendChild(hsetSpan);
        hsetDiv.appendChild(hsetFieldsDiv);

        const hr = document.createElement('hr');
        hr.className = 'no-margin-hr';

        const typeDiv = document.createElement('div');
        typeDiv.className = 'redis-type';

        const typeSpan1 = document.createElement('span');
        typeSpan1.classList.add('label-text', 'fas', 'fa-database');
        typeSpan1.innerHTML = '&nbsp;';

        const typeSpan2 = document.createElement('span');
        typeSpan2.className = 'label-mini-title';
        typeSpan2.textContent = item.type.toString();

        typeDiv.appendChild(typeSpan1);
        typeDiv.appendChild(typeSpan2);

        nameDiv.appendChild(idxSpan);
        nameDiv.appendChild(nameSpan);

        headerDiv.appendChild(nameDiv);
        headerDiv.appendChild(deleteBtn);

        detailDiv.appendChild(headerDiv);
        detailDiv.appendChild(setDiv);
        detailDiv.appendChild(zsetDiv);
        detailDiv.appendChild(hsetDiv);

        infoDiv.appendChild(detailDiv);

        unitDiv.appendChild(infoDiv);
        unitDiv.appendChild(hr);
        unitDiv.appendChild(typeDiv);

        grid.appendChild(unitDiv);
    });
}

/**
 * Redis cache 삭제 method
 * @param type Cache data type index
 * @param key Cache key
 */
function deleteRedisCache(type, key) {
    $.ajax({
        type: 'POST',
        url: '/admin/redis',
        data: JSON.stringify({
            type: type,
            key: key,
            field: null
        }),
        contentType: 'application/json; charset=utf-8',
        success: function () {
            alert('정상적으로 삭제되었습니다.');
            refreshRedisTab();
        },
        error: function () {
            alert('작업중 오류가 발생했습니다.');
        }
    });
}

/**
 * Redis cache 삭제 method - Hash set 필드 삭제
 * @param type Cache data type index
 * @param key Cache key
 * @param field Cache field name
 */
function deleteRedisCacheForHashSetField(type, key, field) {
    $.ajax({
        type: 'POST',
        url: '/admin/redis',
        data: JSON.stringify({
            type: type,
            key: key,
            field: field
        }),
        contentType: 'application/json; charset=utf-8',
        success: function () {
            alert('정상적으로 삭제되었습니다.');
            refreshRedisTab();
        },
        error: function () {
            alert('작업중 오류가 발생했습니다.');
        }
    });
}

refreshSeleniumTab();
refreshRedisTab();
