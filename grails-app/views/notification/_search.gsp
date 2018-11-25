<el:formGroup>
    <el:textField name="title" size="8"  class="" label="${message(code:'notification.title.label',default:'title')}" />
</el:formGroup>

<el:formGroup>
    <el:textField name="text" size="8"  class="" label="${message(code:'notification.text.label',default:'text')}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="fromNotificationDate" size="8"  class="" label="${message(code:'notification.fromNotificationDate.label',default:'fromNotificationDate')}" />
</el:formGroup>

<el:formGroup>
    <el:dateField name="toNotificationDate" size="8"  class="" label="${message(code:'notification.toNotificationDate.label',default:'toNotificationDate')}" />
</el:formGroup>

<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="notification" action="autocompleteNotificationType" name="notificationType.id" label="${message(code:'notification.notificationType.label',default:'notificationType')}" />
</el:formGroup>

<el:hiddenField name="notificationTopic" value="${params.notificationTopicParams}" />

