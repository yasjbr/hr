<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'notification.entity', default: 'Notification List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'Notification List')}" />
    <title>${title}</title>
</head>
<body>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${notification?.title}" type="String" label="${message(code:'notification.title.label',default:'title')}" />
    <lay:showElement value="${notification?.text}" type="String" label="${message(code:'notification.text.label',default:'text')}" />
    <lay:showElement value="${notification?.notificationDate}" type="ZonedDate" label="${message(code:'notification.notificationDate.label',default:'notificationDate')}" />
    <lay:showElement value="${notification?.icon}" type="String" label="${message(code:'notification.icon.label',default:'icon')}" />
    <lay:showElement value="${notification?.notificationType}" type="NotificationType" label="${message(code:'notification.notificationType.label',default:'notificationType')}" />
    <lay:showElement value="${notification?.expireAfter}" type="enum" label="${message(code:'notification.expireAfter.label',default:'expireAfter')}" />
</lay:showWidget>

<g:set var="actions" value="${notification.transientData.actions?.toList()?.sort{it.label}}" />

<g:if test="${actions}">
    <lay:table title="${message(code:'notification.notificationAction.label',default:'notification action')}">
        <lay:tableHead title="${message(code:'notificationAction.label.label')}" />
        <lay:tableHead title="${message(code:'notificationAction.controller.label')}" />
        <lay:tableHead title="${message(code:'notificationAction.action.label')}" />
        <lay:tableHead title="${message(code:'notificationAction.icon.label')}" />
        <lay:tableHead title="${message(code:'notificationAction.notificationParams.label')}" />

        <g:each in="${actions}" var="action">
            <lay:tableRow bean="${action}"  beanProperties="[
                    'label',
                    'controller',
                    'action',
                    'icon',
                    'notificationParams',
            ]" />
        </g:each>

    </lay:table>
</g:if>

<el:row />
<div class="clearfix form-actions text-center">
    <btn:listButton onClick="window.location.href='${createLink(controller:'notification',action:'list')}'"/>
</div>
</body>
</html>