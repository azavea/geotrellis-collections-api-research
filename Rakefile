task default: %[build]

desc "Build project"
task :build do
    puts "Building app container ->"
    sh "docker-compose -f docker-compose.yml run --rm --no-deps app npm install --no-optional --quiet"
end

desc "Run servers"
task :server do
    puts "Starting servers ->"
end

desc "Configure Tmuxinator"
task :tmux do
    puts "Configuring Tmuxinator ->"
    tmuxinator_dir = File.expand_path(Dir.home + "/.tmuxinator")
    if File.exist?(tmuxinator_dir)
        sh "ln -s #{Dir.pwd}/tmux.yml #{tmuxinator_dir}/geotrellis-research.yml"
    else
        puts "Tmuxinator not installed"
    end
end
