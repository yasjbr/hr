

<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "vacationRequest.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">


            <lay:showElement value="${vacationRequest?.vacationType?.descriptionInfo?.localName}" type="VacationType"
                             label="${message(code: 'vacationRequest.vacationType.label', default: 'vacationType')}"/>

            <lay:showElement value="${vacationRequest?.fromDate}" type="ZonedDate"
                             label="${message(code: 'vacationRequest.fromDate.label', default: 'fromDate')}"/>

        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${vacationRequest?.numOfDays}" type="Integer"
                             label="${message(code: 'vacationRequest.numOfDays.label', default: 'numOfDays')}"/>



            <lay:showElement value="${vacationRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'vacationRequest.toDate.label', default: 'toDate')}"/>
        </lay:showWidget>




        <el:row/>
    </lay:widgetBody>
</lay:widget>
