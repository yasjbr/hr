<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'disciplinaryListJudgmentSetup.entity', default: 'DisciplinaryListJudgmentSetup List')}"/>
    <g:set var="title"
           value="${message(code: 'default.show.label', args: [entity], default: 'DisciplinaryListJudgmentSetup List')}"/>
    <title>${title}</title>
</head>

<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton
                onClick="window.location.href='${createLink(controller: 'disciplinaryListJudgmentSetup', action: 'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${disciplinaryListJudgmentSetup?.listNamePrefix}" type="String"
                     label="${message(code: 'disciplinaryListJudgmentSetup.listNamePrefix.label', default: 'listNamePrefix')}"/>
    <lay:showElement value="${disciplinaryListJudgmentSetup?.disciplinaryCategory?.descriptionInfo?.localName}"
                     type="string"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryCategory.label', default: 'disciplinaryCategory')}"/>
    <lay:showElement value="${disciplinaryListJudgmentSetup?.disciplinaryJudgment?.descriptionInfo?.localName}"
                     type="string"
                     label="${message(code: 'disciplinaryListJudgmentSetup.disciplinaryJudgment.label', default: 'disciplinaryJudgment')}"/>
</lay:showWidget>
<el:row/>

<div class="clearfix form-actions text-center">
    <btn:editButton
            onClick="window.location.href='${createLink(controller: 'disciplinaryListJudgmentSetup', action: 'edit', params: [encodedId: disciplinaryListJudgmentSetup?.encodedId])}'"/>
</div>

</body>
</html>