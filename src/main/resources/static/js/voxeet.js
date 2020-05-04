var conferenceId
const main = async (name) => {
    VoxeetSDK.initialize('N2wzZXJrdG1zcTc3cQ==', 'NzRqZ2pocGNmdmNxa2Q5YjZob2FoYWQ0MzU=')
    VoxeetSDK.conference.on('streamAdded', (participant, stream) => {
        addVideoNode(participant, stream);
    })
    try {
        await VoxeetSDK.session.open({name})
        VoxeetSDK.conference.create({ alias: name })
            .then((conference) =>{
                conferenceId = conference
                    return VoxeetSDK.conference.join(conference, {})
            }
            )
            .then(() => {

                VoxeetSDK.conference.startVideo(VoxeetSDK.session.participant).then(
                    ()=>{
                        $('#body').preloader('remove')
                        document.getElementById('video-super-container').style.display='flex'
                          }
                ).catch(e=>console.log(e))



            })
            .catch((e) => {console.log(e);$('#body').preloader('remove')})

    }


    catch(e){
        console.log(e)
    }
}
const callsButton = document.getElementsByClassName('call')
const call = ()=>{
    $('#body').preloader()
    fetch('http://localhost:8080/sendMail/newordn@gmail.com').then(()=>{
        console.log('test')
    })
    main(document.getElementById('directorFirstName').value)
}
for(i=0;i<callsButton.length;i++){
    callsButton[i].addEventListener('click',call)
}


