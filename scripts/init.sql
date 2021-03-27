
CREATE TABLE `playing_info` (
  `id` INTEGER PRIMARY KEY,
  `player_name` varchar(50) NOT NULL,
  `round` int(2) NOT NULL DEFAULT 0,
  `roll` int(2) NOT NULL DEFAULT 0,
  `upper_bonus` int(5) DEFAULT NULL,
  `lower_bonus` int(5) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE `keep_listing` (
  `id` int(2) NOT NULL,
  `game_id` int(20) NOT NULL,
  `dice` int(2) NOT NULL,
  `is_keep` tinyint(1) NOT NULL DEFAULT false,
   UNIQUE(`id`,`game_id`)
);

CREATE TABLE `score_card` (
  `game_id` int(20) NOT NULL,
  `category` varchar(20) NOT NULL,
  `score` int(2) DEFAULT NULL,
   UNIQUE(`game_id`,`category`)
);
