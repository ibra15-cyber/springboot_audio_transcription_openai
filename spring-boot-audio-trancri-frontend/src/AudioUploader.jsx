import axios from "axios";
import React, { useState } from "react";

function AudioUploader() {
  const [transcription, setTranscription] = useState("");
  const [file, setFile] = useState(null);

  const handleFileChange = (e) => {
    setFile(e.target.files[0]); //set the file
  };

  const handleUpload = async () => {
    const formData = new FormData();
    formData.append("file", file); //get the uploaded file and post it below

    try {
      const { data } = await axios.post(
        "http://localhost:8080/api/transcribe/",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      setTranscription(data);
    } catch (e) {
      console.error("Error transcribing request", e);
    }
  };

  return (
    <div className="container">
      <h1>Audio to Text Transcibe</h1>
      <div className="file-input">
        <input type="file" accept="audio/*" onChange={handleFileChange} />
      </div>
      <button className="upload-button" onClick={handleUpload}>
        Upload and Transcribe
      </button>
      <div className="transcription-result">
        <h2>Transcription Result</h2>
        <p>{transcription}</p>
      </div>
    </div>
  );
}

export default AudioUploader;
