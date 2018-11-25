<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "maritalStatusRequest.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">
            <lay:showElement value="${maritalStatusRequest?.id}" type="String"
                             label="${message(code: 'maritalStatusRequest.id.label', default: 'id')}"/>
            <lay:showElement value="${maritalStatusRequest?.requestDate}" type="ZonedDate"
                             label="${message(code: 'maritalStatusRequest.requestDate.label', default: 'requestDate')}"/>

            <lay:showElement value="${maritalStatusRequest?.transientData?.relatedPersonDTO?.localFullName}"
                             type="Long"
                             label="${message(code: 'maritalStatusRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPersonId')}"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${maritalStatusRequest?.threadId}" type="String"
                             label="${message(code: 'maritalStatusRequest.threadId.label', default: 'threadId')}"/>

            <lay:showElement value="${maritalStatusRequest?.requestStatus}" type="enum"
                             label="${message(code: 'maritalStatusRequest.requestStatus.label', default: 'requestStatus')}"/>
        </lay:showWidget>
        <el:row/>
    </lay:widgetBody>
</lay:widget>
