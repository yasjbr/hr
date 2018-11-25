
<div id="${tabEntityName}Div">
    <g:set var="entity" value="${message(code: 'vacancyAdvertisements.entity', default: 'vacancy List')}" />
    <g:set var="tabEntity" value="${message(code: 'vacancy.entity', default: 'vacancy ')}" />
    <g:set var="tabEntities" value="${message(code: 'vacancy.entities', default: 'vacancy ')}" />
    <g:set var="tabList" value="${message(code: 'default.list.label',args:[tabEntities], default: 'list vacancy ')}" />
    <g:set var="tabTitle" value="${message(code: 'default.create.label',args:[tabEntity], default: 'create vacancy ')}" />

            <label>${message(code:'vacancyAdvertisements.empty.label',default: 'empty vacancies')}</label>
</div>