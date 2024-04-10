/**
 * 사용자 탭 관련
 */

const btnRefreshUserTab = document.getElementById('btn-refresh-user-tab');
btnRefreshUserTab.addEventListener('click', refreshUserTab);

/**
 * 사용자 탭 새로고침 버튼 처리 method
 */
function refreshUserTab() {
    $.ajax({
        type: 'GET',
        url: '/admin/user',
        success: function (data) {
            makeUserTab(data);
        }
    });
}

/**
 * 사용자 탭 view 생성용 method
 * @param data user 목록 정보 by ajax
 */
function makeUserTab(data) {
    const cntSpan = document.getElementById('user-count');
    cntSpan.innerHTML = '&#128101;&nbsp;' + data.users.length + '명';

    const grid = document.getElementById('user-grid');
    while (grid.firstChild) {
        grid.removeChild(grid.firstChild); //기존의 하위 목록 삭제
    }

    $.each(data.users, function(index, item) {
        const unitDiv = document.createElement('div');
        unitDiv.className = 'user';

        const infoDiv = document.createElement('div');
        infoDiv.className = 'user-info';

        const detailDiv = document.createElement('div');
        detailDiv.className = 'user-info-detail';

        const headerDiv = document.createElement('div');
        headerDiv.className = 'user-info-detail-header';

        const idDiv = document.createElement('div');
        idDiv.className = 'user-id';

        const idxSpan = document.createElement('span');
        idxSpan.className = 'label-mini-title';
        idxSpan.innerHTML = '[' + (index + 1) + ']&nbsp;'

        const idSpan = document.createElement('span');
        idSpan.id = 'user-id-' + index;
        idSpan.className = 'label-bold-ellipsis-text';
        idSpan.textContent = item.userId;

        const roleSpan = document.createElement('span');
        roleSpan.className = 'label-mini-title';
        roleSpan.innerHTML = '&nbsp;(사용자)';
        if (item.userRoleTypes.includes('ROLE_ADMIN')) {
            roleSpan.classList.add('label-red');
            roleSpan.innerHTML = '&nbsp;(관리자)';
        }

        idDiv.appendChild(idxSpan);
        idDiv.appendChild(idSpan);
        idDiv.appendChild(roleSpan);

        const saveBtn = document.createElement('button');
        saveBtn.classList.add('btn', 'btn-success', 'label-mini-title', 'label-white');
        saveBtn.textContent = '저장';
        saveBtn.onclick = () => {
            saveUser(index);
        }

        headerDiv.appendChild(idDiv);
        headerDiv.appendChild(saveBtn);

        const regSpan = document.createElement('div');
        regSpan.classList.add('label-mini-title', 'user-created-at');
        regSpan.innerHTML = '&#9654;&nbsp; ' + item.createdAt + ' 가입';

        const rolesSpan = document.createElement('div');
        rolesSpan.className = 'label-mini-title';
        rolesSpan.innerHTML = '&#9654;&nbsp; 권한';

        const rolesDiv = document.createElement('div');
        rolesDiv.className = 'user-roles';

        $.each(data.roles, function(idx, itm) {
            const roleDiv = document.createElement('div');
            roleDiv.className = 'user-role';

            const roleChk = document.createElement('input');
            roleChk.id = 'chk-' + index + '-' + idx;
            roleChk.className = 'form-check-input';
            roleChk.type = 'checkbox';
            roleChk.checked = (item.userRoleTypes.includes(itm));

            const roleLbl = document.createElement('label');
            roleLbl.id = 'lbl-' + index + '-' + idx;
            roleLbl.classList.add('form-check-label', 'label-mini-title');
            roleLbl.innerHTML = '&nbsp;' + itm;
            roleLbl.for = 'chk-' + index + '-' + idx;

            roleDiv.appendChild(roleChk);
            roleDiv.appendChild(roleLbl);

            rolesDiv.appendChild(roleDiv);
        });

        detailDiv.appendChild(headerDiv);
        detailDiv.appendChild(regSpan);
        detailDiv.appendChild(rolesSpan);
        detailDiv.appendChild(rolesDiv);

        const hr = document.createElement('hr');
        hr.className = 'no-margin-hr';

        const timeDiv = document.createElement('div');
        timeDiv.className = 'user-modified-at';

        const timeSpan1 = document.createElement('span');
        timeSpan1.classList.add('label-text', 'fas', 'fa-clock');
        timeSpan1.innerHTML = '&nbsp;';

        const timeSpan2 = document.createElement('span');
        timeSpan2.className = 'label-mini-title';
        timeSpan2.textContent = item.modifiedAt;

        timeDiv.appendChild(timeSpan1);
        timeDiv.appendChild(timeSpan2);

        infoDiv.appendChild(detailDiv);

        unitDiv.appendChild(infoDiv);
        unitDiv.appendChild(hr);
        unitDiv.appendChild(timeDiv);

        grid.appendChild(unitDiv);
    });
}

/**
 * 사용자 정보 저장용 method
 * @param idx 목록상 사용자 index
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

/**
 * API 탭 관련
 */

const btnRefreshAPITab = document.getElementById('btn-refresh-api-tab');
btnRefreshAPITab.addEventListener('click', refreshAPITab);

/**
 * API 새로고침 버튼 처리 method
 */
function refreshAPITab() {
    $.ajax({
        type: 'GET',
        url: '/admin/api',
        success: function (data) {
            makeAPITab(data);
        }
    });
}

/**
 * API 탭 view 생성용 method
 * @param data API 목록 정보 by ajax
 */
function makeAPITab(data) {
    const runningUrl = data.runningInfo.url;
    const runningParam1 = data.runningInfo.param1;
    const runningParam2 = data.runningInfo.param2;

    const statusSpan = document.getElementById('api-status');
    statusSpan.innerHTML = runningUrl === null ? '&#9989 작업 가능' : '&#8987 작업중 - ' + runningUrl;

    const grid = document.getElementById('api-grid');
    while (grid.firstChild) {
        grid.removeChild(grid.firstChild); //기존의 하위 목록 삭제
    }

    $.each(data.apis, function(index, item) {
        const unitDiv = document.createElement('div');
        unitDiv.className = 'api';

        const infoDiv = document.createElement('div');
        infoDiv.className = 'api-info';

        const detailDiv = document.createElement('div');
        detailDiv.className = 'api-info-detail';

        const headerDiv = document.createElement('div');
        headerDiv.className = 'api-info-detail-header';

        const urlDiv = document.createElement('div');
        urlDiv.className = 'api-url';

        const idxSpan = document.createElement('span');
        idxSpan.className = 'label-mini-title';
        idxSpan.innerHTML = '[' + (index + 1) + ']&nbsp;'

        const urlSpan = document.createElement('span');
        urlSpan.className = 'label-bold-ellipsis-text';
        urlSpan.textContent = item.url;

        urlDiv.appendChild(idxSpan);
        urlDiv.appendChild(urlSpan);

        const execBtn = document.createElement('button');
        execBtn.classList.add('btn', 'label-mini-title', 'label-white');
        if (runningUrl === null) {
            execBtn.classList.add('btn-success');
            execBtn.textContent = '실행';
        } else {
            if (runningUrl === item.url) {
                execBtn.classList.add('btn-danger');
                execBtn.textContent = '실행중';
            } else {
                execBtn.classList.add('btn-secondary');
                execBtn.textContent = '-';
            }
        }
        execBtn.disabled = (runningUrl !== null);
        execBtn.onclick = () => {
            runScrapAPI(index, item.url, param1Input, param2Input);
        }

        headerDiv.appendChild(urlDiv);
        headerDiv.appendChild(execBtn);

        detailDiv.appendChild(headerDiv);

        const paramsDiv = document.createElement('div');
        paramsDiv.className = 'api-params';

        const param1Div = document.createElement('div');
        param1Div.className = 'param';

        const param1Span = document.createElement('span');
        param1Span.className = 'label-mini-title';
        param1Span.innerHTML = '&#9654;&nbsp; ' + (index === 0 ? 'state=' : 'start=') + '&nbsp;';

        const param1Input = document.createElement('input');
        param1Input.type = 'text';
        param1Input.id = 'api' + (index + 1) + 'Param1';
        param1Input.classList.add('form-control', 'label-mini-title');
        if (runningUrl !== null) {
            param1Input.disabled = true;

            if (runningUrl === item.url) {
                param1Input.value = runningParam1;
            }
        }

        param1Div.appendChild(param1Span);
        param1Div.appendChild(param1Input);

        const param2Div = document.createElement('div');
        param2Div.className = 'param';
        param2Div.style.display = (index === 0 ? 'none' : '');

        const param2Span = document.createElement('span');
        param2Span.className = 'label-mini-title';
        param2Span.innerHTML = '&#9654;&nbsp; end=&nbsp;';

        const param2Input = document.createElement('input');
        param2Input.type = 'text';
        param2Input.id = 'api' + (index + 2) + 'Param2';
        param2Input.classList.add('form-control', 'label-mini-title');
        if (runningUrl !== null) {
            param2Input.disabled = true;

            if (runningUrl === item.url) {
                param2Input.value = runningParam2;
            }
        }

        param2Div.appendChild(param2Span);
        param2Div.appendChild(param2Input);

        paramsDiv.appendChild(param1Div);
        paramsDiv.appendChild(param2Div);

        const hr = document.createElement('hr');
        hr.className = 'no-margin-hr';

        const timeDiv = document.createElement('div');
        timeDiv.className = 'api-time';

        const timeSpan1 = document.createElement('span');
        timeSpan1.classList.add('label-text', 'fas', 'fa-clock');
        timeSpan1.innerHTML = '&nbsp;';

        const timeSpan2 = document.createElement('span');
        timeSpan2.className = 'label-mini-title';
        timeSpan2.textContent = item.modifiedAt;

        timeDiv.appendChild(timeSpan1);
        timeDiv.appendChild(timeSpan2);

        const tableDiv = document.createElement('div');
        tableDiv.className = 'api-table-info';
        tableDiv.style.display = (index === 0 ? 'none' : '');

        const tableSpan1 = document.createElement('span');
        tableSpan1.classList.add('label-mini-title', 'label-text', 'fas', 'fa-database');
        tableSpan1.innerHTML = '&nbsp;';

        const tableSpan2 = document.createElement('span');
        tableSpan2.className = 'label-mini-title';
        tableSpan2.innerHTML = 'MAX(draw_no) =&nbsp;';

        const tableSpan3 = document.createElement('span');
        tableSpan3.classList.add('label-mini-title', 'label-special');
        tableSpan3.textContent = item.maxDrawNo;

        tableDiv.appendChild(tableSpan1);
        tableDiv.appendChild(tableSpan2);
        tableDiv.appendChild(tableSpan3);

        headerDiv.appendChild(urlDiv);
        headerDiv.appendChild(execBtn);

        detailDiv.appendChild(headerDiv);
        detailDiv.appendChild(paramsDiv);

        infoDiv.appendChild(detailDiv);

        unitDiv.appendChild(infoDiv);
        unitDiv.appendChild(hr);
        unitDiv.appendChild(timeDiv);
        unitDiv.appendChild(tableDiv);

        grid.appendChild(unitDiv);
    });
}

///

function chkShopAPIParams(param1) {
    if (param1.value.trim() === '') {
        alert('[state]를 입력해주세요.');
        $(param1).focus();
        return false;

    } else if (!regexUpperCase(param1.value)) {
        alert('[state]는 영문 대문자만 입력가능합니다.');
        param1.value = '';
        $(param1).focus();
        return false;
    }

    return true;
}

function chkWinAPIParams(param1, param2) {
    if (param1.value.trim() === '') {
        alert('[start]를 입력해주세요.');
        $(param1).focus();
        return false;
    } else if (!regexDigit(param1.value)) {
        alert('[start]는 숫자만 입력가능합니다.');
        param1.value = '';
        $(param1).focus();
        return false;
    }

    if (param2.value.trim() === '') {
        alert('[end]를 입력해주세요.');
        $(param2).focus();
        return false;
    } else if (!regexDigit(param2.value)) {
        alert('[end]는 숫자만 입력가능합니다.');
        param2.value = '';
        $(param2).focus();
        return false;
    }

    if (parseInt(param1.value) > parseInt(param2.value)) {
        alert('[start]는 [end]보다 클 수 없습니다.');
        param1.value = '';
        param2.value = '';
        $(param1).focus();
        return false;
    }

    return true;
}

function runScrapAPI(idx, url, param1, param2) {
    const isParamsValid = (idx === 0 ? chkShopAPIParams(param1) : chkWinAPIParams(param1, param2));

    if (!isParamsValid)
        return;

    if (!confirm('작업을 실행하시겠습니까?'))
        return;

    $.ajax({
        type: 'GET',
        url: url,
        data: idx === 0 ? { state: param1.value } : { start: param1.value, end: param2.value },
        success: function () {
            alert('작업이 완료되었습니다.');

            refreshAPITab();
            refreshSeleniumTab();
            refreshRedisTab();
        }
    });

    alert('작업이 요청되었습니다.');

    refreshAPITab();
    refreshSeleniumTab();
    refreshRedisTab();
}

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

            refreshAPITab();
            refreshSeleniumTab();
            refreshRedisTab();
        },
        error: function () {
            alert('작업중 오류가 발생했습니다.');
        }
    });
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

$(function() {
    $('#header').load('/header.html');
    $('#footer').load('/footer.html');
});

refreshUserTab();
refreshAPITab();
refreshSeleniumTab();
refreshRedisTab();
