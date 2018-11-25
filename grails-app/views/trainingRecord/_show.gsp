<lay:showWidget size="12" title="${title}">

    <lay:showElement value="${trainingRecord?.trainingClassification}" type="TrainingClassification" label="${message(code:'trainingRecord.trainingClassification.label',default:'trainingClassification')}" />

    <g:if test="${trainingRecord?.trainingCourse}">
        <lay:showElement value="${trainingRecord?.trainingCourse}" type="TrainingCourse" label="${message(code:'trainingRecord.trainingCourse.label',default:'trainingCourse')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${trainingRecord?.trainingName}" type="String" label="${message(code:'trainingRecord.trainingName.label',default:'trainingName')}" />
    </g:else>

    <lay:showElement value="${trainingRecord?.fromDate}" type="ZonedDate" label="${message(code:'trainingRecord.fromDate.label',default:'fromDate')}" />
    <lay:showElement value="${trainingRecord?.toDate}" type="ZonedDate" label="${message(code:'trainingRecord.toDate.label',default:'toDate')}" />


    <g:if test="${trainingRecord?.transientData?.organizationDTO?.toString()}">
        <lay:showElement value="${trainingRecord?.transientData?.organizationDTO?.toString()}" type="String" label="${message(code:'trainingRecord.organizationId.label',default:'organizationId')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${trainingRecord?.organizationName}" type="String" label="${message(code:'trainingRecord.organizationName.label',default:'organizationName')}" />
    </g:else>


    <g:if test="${trainingRecord?.trainer?.transientData?.personDTO?.toString()}">
        <lay:showElement value="${trainingRecord?.trainer?.transientData?.personDTO?.toString()}" type="String" label="${message(code:'trainingRecord.trainer.label',default:'trainer')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${trainingRecord?.trainerName}" type="String" label="${message(code:'trainingRecord.trainerName.label',default:'trainerName')}" />
    </g:else>

    <lay:showElement value="${trainingRecord?.certificate}" type="String" label="${message(code:'trainingRecord.certificate.label',default:'certificate')}" />


    <lay:showElement value="${trainingRecord?.numberOfTrainee}" type="Long" label="${message(code:'trainingRecord.numberOfTrainee.label',default:'numberOfTrainee')}" />


    <lay:showElement value="${trainingRecord?.period + " " + trainingRecord?.transientData?.unitDTO?.toString()}" type="String" label="${message(code:'trainingRecord.period.label',default:'period')}" />

    <lay:showElement value="${trainingRecord?.note}" type="String" label="${message(code:'trainingRecord.note.label',default:'note')}" />


    <lay:showElement value="${trainingRecord?.transientData?.locationDTO?(trainingRecord?.transientData?.locationDTO?.toString() + "${trainingRecord?.unstructuredLocation?(" - "+trainingRecord?.unstructuredLocation):""}"):""}" type="String" label="${message(code:'trainingRecord.transientData.locationDTO.label',default:'locationId')}" />


</lay:showWidget>