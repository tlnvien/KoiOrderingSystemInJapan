import React, { useState } from "react";
import "./Feedback.css";

const Feedback = () => {
  const [rating, setRating] = useState(0);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState("");
  const [reviews, setReviews] = useState([]);

  const handleStarClick = (ratingValue) => {
    setRating(ratingValue);
  };

  const handleStarHover = (ratingValue) => {
    setHoverRating(ratingValue);
  };

  const handleSubmit = () => {
    if (rating > 0 && comment !== "") {
      const newReview = { rating, comment };
      setReviews([...reviews, newReview]);
      setRating(0);
      setComment("");
    } else {
      alert("Please provide a rating and comment!");
    }
  };

  return (
    <div className="feedback-page">
      <div className="review-container">
        <h2>Rate and Review</h2>

        <div className="star-rating">
          {[1, 2, 3, 4, 5].map((starValue) => (
            <span
              key={starValue}
              className={`star ${
                hoverRating >= starValue || rating >= starValue ? "active" : ""
              }`}
              onClick={() => handleStarClick(starValue)}
              onMouseEnter={() => handleStarHover(starValue)}
              onMouseLeave={() => setHoverRating(0)}
            >
              &#9733;
            </span>
          ))}
        </div>

        <textarea
          value={comment}
          onChange={(e) => setComment(e.target.value)}
          placeholder="Leave your comment here..."
        ></textarea>

        <button onClick={handleSubmit} className="buttonFeedback">
          Submit Review
        </button>

        <div className="review-section">
          <h3>User Reviews</h3>
          <ul>
            {reviews.map((review, index) => (
              <li key={index} className="review-item">
                <div className="stars">
                  {Array(review.rating)
                    .fill()
                    .map((_, i) => (
                      <span key={i} className="active">
                        &#9733;
                      </span>
                    ))}
                </div>
                <p className="comment">{review.comment}</p>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
};

export default Feedback;
