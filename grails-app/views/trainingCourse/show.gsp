<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingCourse.entity', default: 'TrainingCourse List')}" />
    <g:set var="title" value="${message(code: 'default.show.label',args:[entity], default: 'TrainingCourse List')}" />
    <title>${title}</title>
</head>
<body>

<msg:page/>

<div style="margin-top: -46px">
    <div class="widget-toolbar"><div data-toggle="" class="btn-group btn-overlap btn-corner">
        <btn:listButton onClick="window.location.href='${createLink(controller:'trainingCourse',action:'list')}'"/>
    </div></div>
</div>
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${trainingCourse?.arabicDescription}" type="String" label="${message(code:'trainingCourse.arabicDescription.label',default:'arabicDescription')}" />
    <lay:showElement value="${trainingCourse?.courseCode}" type="String" label="${message(code:'trainingCourse.courseCode.label',default:'courseCode')}" />
    <lay:showElement value="${trainingCourse?.descriptionInfo}" type="DescriptionInfo" label="${message(code:'trainingCourse.descriptionInfo.label',default:'descriptionInfo')}" />
    <lay:showElement value="${trainingCourse?.englishDescription}" type="String" label="${message(code:'trainingCourse.englishDescription.label',default:'englishDescription')}" />
    <lay:showElement value="${trainingCourse?.prerequisiteCourses}" type="Set" label="${message(code:'trainingCourse.prerequisiteCourses.label',default:'prerequisiteCourses')}" />
    <lay:showElement value="${trainingCourse?.targetGroup}" type="TargetGroup" label="${message(code:'trainingCourse.targetGroup.label',default:'targetGroup')}" />
    <lay:showElement value="${trainingCourse?.trainerCondition}" type="TrainerCondition" label="${message(code:'trainingCourse.trainerCondition.label',default:'trainerCondition')}" />
    <lay:showElement value="${trainingCourse?.TrainingClassification}" type="TrainingClassification" label="${message(code:'trainingCourse.TrainingClassification.label',default:'TrainingClassification')}" />
    <lay:showElement value="${trainingCourse?.TrainingClassificationEvaluationForm}" type="Set" label="${message(code:'trainingCourse.TrainingClassificationEvaluationForm.label',default:'TrainingClassificationEvaluationForm')}" />
    <lay:showElement value="${trainingCourse?.trainingConditions}" type="Set" label="${message(code:'trainingCourse.trainingConditions.label',default:'trainingConditions')}" />
    <lay:showElement value="${trainingCourse?.trainingObjectives}" type="Set" label="${message(code:'trainingCourse.trainingObjectives.label',default:'trainingObjectives')}" />
    <lay:showElement value="${trainingCourse?.trainingStatus}" type="enum" label="${message(code:'trainingCourse.trainingStatus.label',default:'trainingStatus')}" messagePrefix="EnumTrainingStatus" />
</lay:showWidget>
<el:row />

</body>
</html>