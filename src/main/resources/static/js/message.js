$("#forumMessage").keypress(function(event){
    var keycode = (event.keyCode ? event.keyCode : event.which);
    if(keycode == '13'){
        $("#forumSendBtn").click()
    }
});
const ecoleId = $("#ecoleId").val()
const name = $("#directorFirstName").val()
const sendMessage = async (name,message,avatar,ecoleId)=>{
    try {
        await db.collection(`ecole${ecoleId}`).add({
                name,
                message,
                createdDate: Date.now(),
            avatar
        })
    }
    catch(e)
    {
        console.log(e)
    }
}
const createMessageItem= (name1,avatar,message,date)=>`<div  class="bg-light mb-3 ${name1==name?"align-self-end":"align-self-start"} p-2 forumItem">
<div class="d-flex flex-row justify-content-start align-items-center border-bottom pb-1">
<img src="${avatar}" class="img-fluid avatar mr-1"/>
<span class="font-weight-bold">${name1}</span>
</div>
<div class="d-flex flex-column justify-content-between align-items-end">
<p>${message}</p>
<div class="text-align-right">
<span class="font-italic " style="font-size: 0.7rem">${moment(date).fromNow()}</span>
</div>
</div>

</div>`
const getMessageAndListen = async (ecoleId)=>{
   const chatRoom= $("#chatRoom")
    console.log(ecoleId)
    db.collection(`ecole${ecoleId}`).onSnapshot(function(snapshot) {
            snapshot.docChanges().forEach(function(change) {
                if (change.type === "added") {
                    const data= change.doc.data()
                    chatRoom.append(createMessageItem(data.name,data.avatar,data.message,data.createdDate))
                    chatRoom.scrollTop(chatRoom.prop("scrollHeight"))
                }

            });
        });

}
$("#forum").click(()=>{
    getMessageAndListen(ecoleId)
    $("#forumDiv").css('display','flex')
})
$("#forumCloseBtn").click(()=>{
    $("#forumDiv").css('display','none')
})
$("#forumSendBtn").click(()=>{
    const message = $("#forumMessage").val()
    const avatar = $("#directorAvatar").val()
    if(message.trim()!="") {
        sendMessage(name, message, avatar, ecoleId)
        $("#forumMessage").val("")
    }
})
