<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "childRequest.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">
            <lay:showElement value="${childRequest?.id}" type="String"
                             label="${message(code: 'childRequest.id.label', default: 'id')}"/>
            <lay:showElement value="${childRequest?.requestDate}" type="ZonedDate"
                             label="${message(code: 'childRequest.requestDate.label', default: 'requestDate')}"/>

            <lay:showElement value="${childRequest?.transientData?.relatedPersonDTO?.localFullName}"
                             type="Long"
                             label="${message(code: 'childRequest.transientData.relatedPersonDTO.localFullName.label', default: 'relatedPersonId')}"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${childRequest?.threadId}" type="String"
                             label="${message(code: 'childRequest.threadId.label', default: 'threadId')}"/>

            <lay:showElement value="${childRequest?.requestStatus}" type="enum"
                             label="${message(code: 'childRequest.requestStatus.label', default: 'requestStatus')}"/>
        </lay:showWidget>
        <el:row/>
    </lay:widgetBody>
</lay:widget>
