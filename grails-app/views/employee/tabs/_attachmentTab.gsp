<g:render template="/attachment/attachmentTab" model="[
        tabEntityName:tabEntityName,
        tabEntityCode:'employee.entity',
        entityId:'',
        searchListMap:[
                ['operationType':'EMPLOYEE',entityId:entityId],
                ['operationType':'PERSON',entityId:holderPersonId]
        ]
]" />