<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "suspensionRequest.label")}">

    <lay:widgetBody>

        <lay:showWidget size="6">
            <lay:showElement value="${suspensionRequest?.suspensionType}" type="Enum"
                             label="${message(code: 'suspensionRequest.suspensionType.label', default: 'suspensionType')}"/>

            <lay:showElement value="${suspensionRequest?.fromDate}" type="ZonedDate"
                             label="${message(code: 'suspensionRequest.fromDate.label', default: 'fromDate')}"/>
        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${suspensionRequest?.periodInMonth}" type="Integer"
                             label="${message(code: 'suspensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>
            <lay:showElement value="${suspensionRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'suspensionRequest.toDate.label', default: 'toDate')}"/>
        </lay:showWidget>
        <el:row/>

    </lay:widgetBody>
</lay:widget>
