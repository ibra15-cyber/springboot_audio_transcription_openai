package com.audio.transcribe;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/transcribe/")
public class TransmissionController {

    //initializing the api key as the request reaches our controller
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    //passing the key at initializing time
    public TransmissionController(@Value("${spring.ai.openai.api-key}") String apiKey) {
        OpenAiAudioApi audioApi = new OpenAiAudioApi(apiKey); //creating an object of the key to access audioapi
        this.transcriptionModel = new OpenAiAudioTranscriptionModel(audioApi); //creating obj of the transcription model
    }

    @PostMapping
    //when a multimedia audio file is passed to this api
    public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("audio", ".wav"); //create a temporal audio file
        file.transferTo(tempFile); //then transfer all the data in the file sent to the file created

        //set the options for the model
        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                .withLanguage("en")
                .withTemperature(0f)
                .build();

        FileSystemResource audioFile = new FileSystemResource(tempFile); //make a file system of the audio

        AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions); //pass the audiofile and the options to the audio prompt
        AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest); //call our model with our request and save the feedback

        tempFile.delete(); //delete the temporal file

        return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK); //return to the user the output and the status code of ok
    }
}
