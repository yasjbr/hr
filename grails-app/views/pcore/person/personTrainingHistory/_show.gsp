<lay:showWidget size="12" title="${title}">


    <lay:showElement value="${personTrainingHistory?.trainingCategory}" type="TrainingCategory" label="${message(code:'personTrainingHistory.trainingCategory.label',default:'trainingCategory')}" />

    <lay:showElement value="${personTrainingHistory?.trainingName}" type="String" label="${message(code:'personTrainingHistory.trainingName.label',default:'trainingName')}" />

    <lay:showElement value="${personTrainingHistory?.trainingFromDate}" type="ZonedDate" label="${message(code:'personTrainingHistory.trainingFromDate.label',default:'trainingFromDate')}" />

    <lay:showElement value="${personTrainingHistory?.trainingToDate}" type="ZonedDate" label="${message(code:'personTrainingHistory.trainingToDate.label',default:'trainingToDate')}" />

    <g:if test="${personTrainingHistory?.organization}">
        <lay:showElement value="${personTrainingHistory?.organization}" type="Organization" label="${message(code:'personTrainingHistory.organization.label',default:'organization')}" />
    </g:if>
    <g:else>
        <lay:showElement value="${personTrainingHistory?.instituteName}" type="String" label="${message(code:'personTrainingHistory.instituteName.label',default:'instituteName')}" />
    </g:else>

    <g:if test="${personTrainingHistory?.trainer}">
        <lay:showElement value="${personTrainingHistory?.trainer}" type="Person" label="${message(code:'personTrainingHistory.trainer.label',default:'trainer')}" />

    </g:if>
    <g:else>
        <lay:showElement value="${personTrainingHistory?.trainerName}" type="String" label="${message(code:'personTrainingHistory.trainerName.label',default:'trainerName')}" />
    </g:else>


    <lay:showElement value="${personTrainingHistory?.trainingDegree}" type="TrainingDegree" label="${message(code:'personTrainingHistory.trainingDegree.label',default:'trainingDegree')}" />

    <lay:showElement value="${personTrainingHistory?.note}" type="String" label="${message(code:'personTrainingHistory.note.label',default:'note')}" />

    <lay:showElement value="${personTrainingHistory?.location?(personTrainingHistory?.location?.toString() + "${personTrainingHistory?.unstructuredLocation?(" - "+personTrainingHistory?.unstructuredLocation):""}"):""}" type="String" label="${message(code:'personTrainingHistory.location.label',default:'location')}" />

</lay:showWidget>